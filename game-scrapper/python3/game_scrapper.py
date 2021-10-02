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
        """,
        """
        ALTER TABLE games ADD COLUMN IF NOT EXISTS steam_price INT DEFAULT 0
        """,
        """
        ALTER TABLE games ADD COLUMN IF NOT EXISTS parent_game_id BIGINT DEFAULT 0
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


async def pick_game_from_steam(app_id, app_name):
    logging.info(f"Pick game by app_id = {app_id}")

    steam_game_link = f"https://store.steampowered.com/app/{app_id}/"

    try:
        game_html_page = requests.get(steam_game_link).text

        html_parser = BeautifulSoup(game_html_page, 'html')

        title = html_parser.title.get_text()

        # hack for remove last string contains ' on Steam'
        if title[:-9] == app_name:
            media_link = html_parser.find("link", rel="image_src").get("href")

            sub_games = []

            purchase_html_elements = html_parser.find_all("div", "game_area_purchase_game_wrapper")

            for element in purchase_html_elements:
                price = int(element.find("div", "game_purchase_price price").get("data-price-final")) / 100
                logging.info(f"Price of subgame = {price}")

                title = element.h1.text[4:]
                logging.info(f"Title of subgame = {title}")

                sub_games.append({'price': price, 'title': title})

            logging.info(f"title = {title}")
            logging.info(f"media_link = {media_link}")
        else:
            logging.warning(f"App with id = {app_id} not exists")
            return None

    except Exception as error:
        logging.error(f"Error pick game by app_id={app_id}: {error}")
        return None

    return media_link, steam_game_link, sub_games


async def is_game_exists_in_database(app_id):
    cursor.execute("SELECT id FROM games WHERE steam_app_id = %s", (app_id,))
    return cursor.rowcount != 0


async def save_steam_game_to_database(app_id: int, app_name: str, steam_game_link: str, price: int) -> int:
    game_exists = await is_game_exists_in_database(app_id)

    if game_exists:
        cursor.execute(
            """
            UPDATE games SET name = %s, steam_app_id = %s, steam_app_link = %s, steam_price = %s WHERE steam_app_id = %s
            """,
            [app_name, app_id, steam_game_link, price, app_id]
        )
    else:
        cursor.execute(
            """
            INSERT INTO games(name, steam_app_id, steam_app_link, steam_price) VALUES(%s, %s, %s, %s)
            """,
            [app_name, app_id, steam_game_link, price]
        )

    model = await find_game_by_name(name=app_name)

    return model[0]


async def is_media_link_exists(media_link: str) -> bool:
    cursor.execute(
        """
        SELECT id FROM media_links WhERE link = %s
        """,
        [media_link]
    )
    return cursor.rowcount != 0


async def save_media_link(media_link: str, game_db_model_id: int):
    media_link_exists = await is_media_link_exists(media_link=media_link)

    if not media_link_exists:
        cursor.execute(
            """
            INSERT INTO media_links(link, game_id) VALUES(%s, %s)
            """,
            [media_link, game_db_model_id]
        )


async def save_game_to_database(app_id: int, app_name: str, media_link: str, steam_game_link: str, sub_games: list):
    logging.info(f"Save game {app_id}, {app_name} to database...")

    try:
        logging.info(f'game name = {app_name}, subgames = {sub_games}')

        exactly_price = 0

        if len(sub_games) != 0:
            # first_price = sub_games[0].price
            for sub_game in sub_games:
                if sub_game['title'] == app_name:
                    exactly_price = sub_game['price']

        game_db_model_id = await save_steam_game_to_database(
            app_id=app_id,
            app_name=app_name,
            steam_game_link=steam_game_link,
            price=exactly_price
        )

        for sub_game in sub_games:
            if sub_game['title'] != app_name:
                await save_steam_game_to_database(
                    app_id=app_id,
                    app_name=sub_game['title'],
                    steam_game_link=steam_game_link,
                    price=sub_game['price']
                )

        logging.info(f"game id = {game_db_model_id}")

        await save_media_link(media_link, game_db_model_id)

        db_connection.commit()

        logging.info(f"Save game {app_id}, {app_name} to database sucessfully")
    except (Exception, psycopg2.DatabaseError) as error:
        db_connection.rollback()
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
    games = get_steam_games()['applist']['apps']

    for game in games:
        asyncio.create_task(scrap_game_from_steam(game))


async def scrap_game_from_steam(game):
    # pick the game
    logging.info(f"Pick game = {game}")
    app_id = game["appid"]
    app_name = game["name"]

    media_link, steam_game_link, sub_games = await pick_game_from_steam(app_id, app_name) or (None, None, None)

    # save to database
    if media_link is not None and steam_game_link is not None and sub_games is not None:
        await save_game_to_database(app_id, app_name, media_link, steam_game_link, sub_games)


if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s - %(levelname)s - %(message)s', datefmt='%m/%d/%Y %I:%M:%S %p',
                        level=logging.INFO)
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
