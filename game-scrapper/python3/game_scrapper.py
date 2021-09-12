import requests
import psycopg2
from configparser import ConfigParser
from bs4 import BeautifulSoup


def get_config(filename, section):
    print("Get config...")
    parser = ConfigParser()
    parser.read(filename)

    config = {}
    if parser.has_section(section):
        params = parser.items(section)
        for param in params:
            config[param[0]] = param[1]
    else:
        raise Exception('Section {0} not found in the {1} file'.format(section, filename))

    print(config)

    print("Get config successfully")
    return config


def connect_to_database(config) -> tuple:
    print("Connect to database...")
    # connection = psycopg2.connect(database="game_picker", user="bravo", password="bravo", host="pi41", port=5432)
    connection = psycopg2.connect(**config)

    print("Connect to database successfully")
    return (connection, connection.cursor())


def close_connection_to_database():
    print("Close database connection...")
    db_connection.close()
    cursor.close()
    print("Close database connection successfully")


def create_tables_in_database():
    print("Create tables in database...")

    create_table_commands = (
        """
        CREATE TABLE IF NOT EXISTS games(
            id BIGSERIAL PRIMARY KEY,
            name TEXT,
            steam_app_id BIGINT,
            steam_app_link TEXT
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS media_links(
            id BIGSERIAL PRIMARY KEY,
            link TEXT,
            game_id BIGINT references games(id)
        )
        """
    )

    for create_table_command in create_table_commands:
        cursor.execute(create_table_command)

    db_connection.commit()

    print("Create tables in database successfully")


def get_steam_games() -> str:
    print("Get steam games...")
    response = requests.get("http://api.steampowered.com/ISteamApps/GetAppList/v0002/").json()
    print("Get steam games successfully")
    return response


def pick_game_from_steam(app_id, app_name) -> tuple:
    print(f"Pick game by app_id = {app_id}")

    steam_game_link = f"https://store.steampowered.com/app/{app_id}/"

    try:
        game_html_page = requests.get(steam_game_link).text
    
        html_parser = BeautifulSoup(game_html_page, 'html')

        title = html_parser.title.get_text()

        # hack for remove last string contains ' on Steam'
        if title[:-9] == app_name:
            media_link = html_parser.find("link", rel="image_src").get("href")

            print(f"title = {title}")
            print(f"media_link = {media_link}")
        else:
            print(f"App with id = {app_id} not exists")
            return None
    
    except Exception as error:
        print(f"Error pick game by app_id={app_id}: {error}")
        return None

    return (media_link, steam_game_link)


def is_game_exists_in_database(app_id):
    cursor.execute("SELECT id FROM games WHERE steam_app_id = %s", (app_id))
    return cursor.rowcount != 0


def save_game_to_database(app_id, app_name, media_link, steam_game_link):
    print(f"Save game {app_id}, {app_name} to database...")

    try:
        if is_game_exists_in_database(app_id):
            print(f"Such game {app_id}, {app_name} already exists")
            return None
        
        game_id = cursor.execute(
            """
            INSERT INTO games(name, steam_app_id, steam_app_link) VALUES(%s, %s, %s)
            """,
            (app_id, app_name, steam_game_link)
        )

        cursor.execute(
            """
            INSERT INTO media_links(link, game_id) VALUES(%s, %s)
            """,
            (media_link, game_id)
        )

        db_connection.commit()

        print(f"Save game {app_id}, {app_name} to database sucessfully")
    except (Exception, psycopg2.DatabaseError) as error:
        print(f"Can not save game with {app_id}, {app_name} to database: {error}")


def scrap_games_from_steam():
    """
    1 получить список игр
    2 найти по каждой игре информацию
    3 сохранить в бд
    """
    games = get_steam_games()["applist"]["apps"]

    for game in games:
        # pick the game
        print(f"game = {game}")
        app_id = game["appid"]
        app_name = game["name"]

        media_link, steam_game_link = pick_game_from_steam(app_id, app_name) or (None, None)

        # save to database
        if media_link is not None and steam_game_link is not None:
            save_game_to_database(app_id, app_name, media_link, steam_game_link)


if __name__ == '__main__':
    print("Game scrapper start")

    try:
        database_config = get_config(filename='database.ini', section='postgresql')
        db_connection, cursor = connect_to_database(config=database_config)
        create_tables_in_database()
        scrap_games_from_steam()
    except (Exception, psycopg2.DatabaseError) as error:
        print(f"Global error: {error}")
    finally:
        if db_connection is not None and cursor is not None:
            close_connection_to_database()
