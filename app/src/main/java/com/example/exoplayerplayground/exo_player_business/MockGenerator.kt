package com.example.exoplayerplayground.exo_player_business

object MockGenerator {

    val image1 =
        "https://cdn-beta.dojoin.com/upload/permanent/thumbnail/e765239b-4fc6-48f9-9396-165dbb6c01e1.jpg"
    val image2 =
        "https://cdn-beta.dojoin.com/upload/permanent/thumbnail/5361ca78-badc-4aca-94f8-5047d91b01b7.jpg"
    val image3 =
        "https://cdn-beta.dojoin.com/upload/permanent/thumbnail/3a4a0073-35b6-43c7-b08d-0a528257d480.jpg"
    val image4 = "https://cdn.dojoin.com/upload/permanent/category-icons/9639329a-e550-4ce1-8457-d9c428037c6e.jpg"
    val videoExt = ".mp4"
    val jpgExt = ".jpg"
    val url1 =
        "https://cdn-beta.dojoin.com/upload/permanent/service-video/c274d6a3-213d-42f3-8094-06fa71e05acf/95135982-ceaa2c69-dd3e-4885-855b-bd5b06047919-220516031222"
    val url2 =
        "https://cdn-beta.dojoin.com/upload/permanent/service-video/772ae829-5a49-4657-980e-793e9efa8786/44512909-d192af7c-0533-49a1-b045-1b93a35fa54c-220501010058"
    val url3 =
        "https://cdn-beta.dojoin.com/upload/permanent/service-video/6e6eec3b-6ec4-4e4d-b3db-c885b4a73279/52833455-76a115fe-d7ce-4df4-b9cc-aa85895fb432-220614005710"

    val colorOrangeUrl="https://yarwoodleather.com/wp-content/uploads/2016/12/Yarwood-Leather-Style-Bright-Orange-01-scaled.jpg"

    val colorGreenUrl ="https://img.buzzfeed.com/buzzfeed-static/static/2020-02/26/18/enhanced/54720c37345e/enhanced-899-1582743530-7.png?output-format=jpg&output-quality=auto"

    val colorBlueUrl ="https://media.tarkett-image.com/large/TH_26513017_001.jpg"

    val colorGrayUrl ="https://htmlcolorcodes.com/assets/images/colors/light-gray-color-solid-background-1920x1080.png"





    val mediaList = listOf(
        MockMediaResponse("", 0, colorOrangeUrl, url3 + videoExt, url3 + jpgExt),
        MockMediaResponse("", 0, colorGreenUrl, url2 + videoExt, url2 + jpgExt),
        MockMediaResponse("", 0, colorBlueUrl, url1 + videoExt, url1 + jpgExt),
        MockMediaResponse("", 1, colorGrayUrl, url1 + videoExt, url1 + jpgExt),
        MockMediaResponse("", 0, colorGreenUrl, url3 + videoExt, url3 + jpgExt),
        MockMediaResponse("", 1, colorOrangeUrl, url2 + videoExt, url2 + jpgExt),
        MockMediaResponse("", 0, colorGrayUrl, url1 + videoExt, url1 + jpgExt)
    )


}


data class MockMediaResponse(
    val id: String,
    val type: Int,//0 for image 1 for player
    val image: String,
    val videoUrl: String,
    val thumbnailUrl: String
)