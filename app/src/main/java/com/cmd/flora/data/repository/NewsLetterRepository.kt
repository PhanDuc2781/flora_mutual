package com.cmd.flora.data.repository

import com.cmd.flora.application.isInternetAvailable
import com.cmd.flora.application.showNetworkError
import com.cmd.flora.data.model.NewsLetter
import com.cmd.flora.data.network.response.Prefecture
import com.cmd.flora.data.network.request.Area.Companion.FUKUSHIMA
import com.cmd.flora.data.network.request.Area.Companion.GIFU
import com.cmd.flora.view.newsletter.NewsLetterFragment.Companion.UL_CLASS
import com.cmd.flora.view.newsletter.NewsLetterFragment.Companion.URL_PREFECTURE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import javax.inject.Inject

interface NewsLetterRepository {
    suspend fun getNewsLetter(
        index: Int,
        reload: Boolean
    ): Result<List<NewsLetter>?>

    suspend fun getPrefecture(): List<Prefecture>

}

class NewsLetterRepositoryImpl @Inject constructor() :
    NewsLetterRepository {

    private var document: Document? = null

    private suspend fun getDocumentIfNeed(reload: Boolean): Document? {
        if (!isInternetAvailable) {
            showNetworkError()
            throw NoInternet
        }
        if (!reload && document != null) return document
        return try {
            val newDocument: Document = withContext(Dispatchers.IO) {
                Jsoup.connect(URL_PREFECTURE).get()
            }
            this.document = newDocument
            newDocument
        } catch (e: IOException) {
            null
        }
    }

    override suspend fun getNewsLetter(
        index: Int,
        reload: Boolean
    ): Result<List<NewsLetter>?> {
        return try {
            document = getDocumentIfNeed(reload)
            document?.let {
                val ulElements = it.getElementsByAttributeValue("class", UL_CLASS)
                val ulElement = ulElements.getOrNull(index)
                val liElements = ulElement?.getElementsByTag("li")

                val listPrefectureElement = liElements?.map {
                    val itemText = it.text()
                    val removeSpaceItemText = itemText.replace("  ", " ")

                    val imgElement = it.getElementsByTag("img").firstOrNull()
                    val itemImageUrl = imgElement?.attr("src")

                    val pdfElement = it.getElementsByTag("a").firstOrNull()
                    val itemPdfUrl = pdfElement?.attr("href")
                    NewsLetter(
                        issue_at = removeSpaceItemText,
                        image = itemImageUrl,
                        url = itemPdfUrl
                    )
                }

                return@let Result.success(listPrefectureElement ?: emptyList<NewsLetter>())
            } ?: Result.success(emptyList())
        } catch (e: NoInternet) {
            return Result.failure(e)
        } catch (e: IOException) {
            e.printStackTrace()
            return Result.failure(e)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }


    override suspend fun getPrefecture(): List<Prefecture> {
        return listOf(
            Prefecture(label = FUKUSHIMA, value = "宮城県・福島県版"),
            Prefecture(label = GIFU, value = "岐阜県版"),
        )
    }

}