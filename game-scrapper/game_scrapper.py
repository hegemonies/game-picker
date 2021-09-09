import requests

def get_steam_games():
    return requests.get("http://api.steampowered.com/ISteamApps/GetAppList/v0002/").json()

def main():
    response = get_steam_games()
    print(len(response["applist"]["apps"]))

main()
