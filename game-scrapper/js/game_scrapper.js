const http = require("http");
const url = "http://api.steampowered.com/ISteamApps/GetAppList/v0002/";

http.get(url, response => {
    console.log("get steam apps, http code: " + response.statusCode)

    if (response.statusCode == 200) {
        let data = ''
        response.on('data', chunk => {
            const result = JSON.parse(JSON.stringify(chunk.toString()))
            data += result;
        })
        response.on('end', () => {
            data = JSON.parse(data);
            // data.applist.apps.forEach(app => { console.log(app)});
            console.log('length of applist is', data.applist.apps.length)
        });
    }
})
