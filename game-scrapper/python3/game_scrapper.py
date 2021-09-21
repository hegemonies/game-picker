import requests
import psycopg2
from configparser import ConfigParser
from bs4 import BeautifulSoup
import logging
import asyncio


def get_config(filename, section):
    logging.info("Get config...")
    parser = ConfigParser()
    parser.read(filename)

    config = {}
    if parser.has_section(section):
        params = parser.items(section)
        for param in params:
            config[param[0]] = param[1]
    else:
        raise Exception('Section {0} not found in the {1} file'.format(section, filename))

    logging.info(config)

    logging.info("Get config successfully")

    return config


def connect_to_database(config) -> tuple:
    logging.info("Connect to database...")

    connection = psycopg2.connect(**config)

    logging.info("Connect to database successfully")

    return (connection, connection.cursor())


def close_connection_to_database():
    logging.info("Close database connection...")
    cursor.close()
    db_connection.close()
    logging.info("Close database connection successfully")


def create_tables_in_database():
    logging.info("Create tables in database...")

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

    logging.info("Create tables in database successfully")


def get_steam_games() -> str:
    logging.info("Get steam games...")
    response = requests.get("http://api.steampowered.com/ISteamApps/GetAppList/v0002/").json()
    logging.info("Get steam games successfully")
    return response


async def pick_game_from_steam(app_id, app_name) -> tuple:
    logging.info(f"Pick game by app_id = {app_id}")

    steam_game_link = f"https://store.steampowered.com/app/{app_id}/"

    try:
        game_html_page = requests.get(steam_game_link).text
    
        html_parser = BeautifulSoup(game_html_page, 'html')

        title = html_parser.title.get_text()

        # hack for remove last string contains ' on Steam'
        if title[:-9] == app_name:
            media_link = html_parser.find("link", rel="image_src").get("href")

            logging.info(f"title = {title}")
            logging.info(f"media_link = {media_link}")
        else:
            logging.warning(f"App with id = {app_id} not exists")
            return None
    
    except Exception as error:
        logging.error(f"Error pick game by app_id={app_id}: {error}")
        return None

    return (media_link, steam_game_link)


def is_game_exists_in_database(app_id):
    cursor.execute("SELECT id FROM games WHERE steam_app_id = %s", (app_id,))
    return cursor.rowcount != 0


async def save_game_to_database(app_id, app_name, media_link, steam_game_link):
    logging.info(f"Save game {app_id}, {app_name} to database...")

    try:
        if is_game_exists_in_database(app_id):
            logging.warning(f"Such game {app_id}, {app_name} already exists")
            return None

        cursor.execute(
            """
            INSERT INTO games(name, steam_app_id, steam_app_link) VALUES(%s, %s, %s)
            """,
            [app_name, app_id, steam_game_link]
        )

        game_model = await find_game_by_name(name=app_name)

        logging.info(f"game id = {game_model[0]}")

        cursor.execute(
            """
            INSERT INTO media_links(link, game_id) VALUES(%s, %s)
            """,
            [media_link, game_model[0]]
        )

        db_connection.commit()

        logging.info(f"Save game {app_id}, {app_name} to database sucessfully")
    except (Exception, psycopg2.DatabaseError) as error:
        logging.error(f"Can not save game with {app_id}, {app_name} to database: {error}")


async def find_game_by_name(name: str):
    cursor.execute(
        """
        SELECT * FROM games WHERE name = %s
        """,
        [name]
    )
    
    return cursor.fetchone()


async def scrap_games_from_steam():
    """
    1 получить список игр
    2 найти по каждой игре информацию
    3 сохранить в бд
    """
    games = get_steam_games()["applist"]["apps"]

    for game in games:
        asyncio.create_task(scrap_game_from_steam(game))


async def scrap_game_from_steam(game):
    # pick the game
    logging.info(f"Pick game = {game}")
    app_id = game["appid"]
    app_name = game["name"]

    media_link, steam_game_link = await pick_game_from_steam(app_id, app_name) or (None, None)

    # save to database
    if media_link is not None and steam_game_link is not None:
        await save_game_to_database(app_id, app_name, media_link, steam_game_link)


if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s %(message)s', datefmt='%m/%d/%Y %I:%M:%S %p', level=logging.INFO)
    logging.info("Game scrapper start")

    try:
        database_config = get_config(filename='database.ini', section='postgresql')
        db_connection, cursor = connect_to_database(config=database_config)
        create_tables_in_database()
        asyncio.run(scrap_games_from_steam())
    except (Exception, psycopg2.DatabaseError) as error:
        logging.error(f"Global error: {error}")
    finally:
        if db_connection is not None and cursor is not None:
            close_connection_to_database()
