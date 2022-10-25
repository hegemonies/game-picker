package ru.twoshoes.gamepicker.service

import arrow.core.getOrHandle
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import ru.twoshoes.gamepicker.service.pageparsing.IPageParsingService
import ru.twoshoes.gamepicker.service.steam.SteamService

@SpringBootTest
@ActiveProfiles("test")
class SteamPageParsingServiceTest {

    @Autowired
    @Qualifier(value = "steamPageParsingService")
    lateinit var steamPageParsingService: IPageParsingService

    @Autowired
    lateinit var steamService: SteamService

    @Test
    fun `first test`(): Unit = runBlocking {
        val gameId = 740130L

        val page = steamService.getHtmlPage(gameId).getOrHandle { error ->
            Assertions.fail(error.message)
        }

        val htmlDocument = steamPageParsingService.parsePage(page).getOrHandle { error ->
            Assertions.fail(error.message)
        }

        val description = steamPageParsingService.getDescription(htmlDocument).getOrHandle { error ->
            Assertions.fail(error.message)
        }

        val developer = steamPageParsingService.getDeveloper(htmlDocument).getOrHandle { error ->
            Assertions.fail(error.message)
        }

        val publisher = steamPageParsingService.getPublisher(htmlDocument).getOrHandle { error ->
            Assertions.fail(error.message)
        }

        val price = steamPageParsingService.getPrice(htmlDocument).getOrHandle { error ->
            Assertions.fail(error.message)
        }

        val title = steamPageParsingService.getTitle(htmlDocument).getOrHandle { error ->
            Assertions.fail(error.message)
        }

        val mediaLinks = steamPageParsingService.getMediaLinks(htmlDocument, gameId).getOrHandle { error ->
            Assertions.fail(error.message)
        }

        val releaseDate = steamPageParsingService.getReleaseDate(htmlDocument).getOrHandle { error ->
            Assertions.fail(error.message)
        }

        println(
            "!!! !!! description=$description, " +
                    "developer=$developer, " +
                    "publisher=$publisher, " +
                    "price=$price, " +
                    "title=$title, " +
                    "mediaLinks=$mediaLinks, " +
                    "releaseDate=$releaseDate"
        )
    }
}
