package me.kosert.vixiarz.searcher

class SearchedVideo(
    val query: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)

class SearchResponseModel(
    //val originalQuery: String, //'szklanki'
    val correctedQuery: String, //'szklanki'
    val items: List<Video>
) {

    val model : SearchedVideo?
        get() {
            val video = items.firstOrNull() ?: return null
            return SearchedVideo(
                correctedQuery,
                video.title,
                video.url,
                video.bestThumbnail.url
            )
        }

    class Video(
//    type: 'video',
        val title: String, //'Young Leosia - Szklanki',
//    id: 'CYiGyaJyPMk',
        val url: String, //'https://www.youtube.com/watch?v=CYiGyaJyPMk',
        val bestThumbnail: Thumbnail, //[Object],
        //val thumbnails: [Array],
        //author: [Object],
        //views: 10963706,
        //duration: '2:51',
    )

    class Thumbnail(val url: String)
}

