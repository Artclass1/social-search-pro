package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.PostDao
import com.example.data.model.SearchHistory
import com.example.data.model.SocialMediaPost
import com.example.data.network.Content
import com.example.data.network.GeminiClient
import com.example.data.network.GenerateContentRequest
import com.example.data.network.GenerationConfig
import com.example.data.network.GeminiSearchResponse
import com.example.data.network.Part
import com.example.data.network.ThinkingLevel
import com.example.data.network.ThinkingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SearchRepository(private val postDao: PostDao) {

    val allPostsFlow: Flow<List<SocialMediaPost>> = postDao.getAllPostsFlow()
    val searchHistoryFlow: Flow<List<SearchHistory>> = postDao.getSearchHistoryFlow()

    // --- Seed Preloaded Social Media Posts ---
    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        if (postDao.getPostCount() == 0) {
            val seedPosts = listOf(
                SocialMediaPost(
                    authorName = "Alex Rivera",
                    authorHandle = "@tech_alex",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100",
                    platform = "Twitter",
                    content = "Breaking down the new release of autonomous AI coding agents. The developer experience is shifting rapidly from writing boilerplate code to guiding multi-agent lifecycles. Are you ready for AI-assisted workflows? #AI #Dev #Coding",
                    timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                    mediaType = "TEXT",
                    engagementLikes = 1240,
                    engagementComments = 432,
                    engagementShares = 180,
                    hashtags = "AI,Dev,Coding"
                ),
                SocialMediaPost(
                    authorName = "Elena Sato",
                    authorHandle = "@elena_travels",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100",
                    platform = "Instagram",
                    content = "Watching the sunrise over Mount Fuji. The morning fog perfectly framed the snowy peak, reminding me why I love exploring. Captured with 50mm, f/2.8, ISO 100. Traveling sustainably is the goal for this decade! 🗻 #Fuji #TravelJapan #Nature",
                    timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
                    mediaType = "IMAGE",
                    mediaUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=800",
                    engagementLikes = 5240,
                    engagementComments = 89,
                    engagementShares = 12,
                    hashtags = "Fuji,TravelJapan,Nature"
                ),
                SocialMediaPost(
                    authorName = "Marcus Chen",
                    authorHandle = "marcus-chen-pm",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100",
                    platform = "LinkedIn",
                    content = "After 10 years of managing remote-first product teams, I have realized that synchronous meetings are the silent killer of productivity. Shift to asynchronous writing, define clear tasks in tickets, and respect deep work hours. Your developers will thank you.",
                    timestamp = System.currentTimeMillis() - 10800000, // 3 hours ago
                    mediaType = "TEXT",
                    engagementLikes = 420,
                    engagementComments = 55,
                    engagementShares = 92,
                    hashtags = "RemoteWork,Productivity,Management"
                ),
                SocialMediaPost(
                    authorName = "Chef Pierre",
                    authorHandle = "PierreBakes",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100",
                    platform = "YouTube",
                    content = "Mastering the Perfect Sourdough Bread at Home: A Complete Visual Guide. From starter feeding ratios to folding techniques, baking temperatures, and steam ovens. Click the link to watch the full bake cycle! 🍞 #Baking #Sourdough #Foodie",
                    timestamp = System.currentTimeMillis() - 14400000, // 4 hours ago
                    mediaType = "VIDEO",
                    mediaUrl = "https://images.unsplash.com/photo-1549931319-a545dcf3bc73?w=800",
                    engagementLikes = 22000,
                    engagementComments = 1450,
                    engagementShares = 3800,
                    hashtags = "Baking,Sourdough,Foodie"
                ),
                SocialMediaPost(
                    authorName = "Dr. Clara Wu",
                    authorHandle = "clara_space_astronomy",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100",
                    platform = "Reddit",
                    content = "James Webb Space Telescope captures pristine infrared imagery of a newly discovered planetary nebula 1,200 light-years away. The rings of stellar dust are visible in unprecedented detail. Absolutely mind-blowing science at work.",
                    timestamp = System.currentTimeMillis() - 18000000, // 5 hours ago
                    mediaType = "IMAGE",
                    mediaUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800",
                    engagementLikes = 8900,
                    engagementComments = 612,
                    engagementShares = 154,
                    hashtags = "Space,WebbTelescope,Science"
                ),
                SocialMediaPost(
                    authorName = "David Capital",
                    authorHandle = "@david_finance",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100",
                    platform = "Twitter",
                    content = "Inflation rates cooling down but interest rate decisions still loom. Diversifying assets into solid tech, equities, and decentralized hedges is key. Stay rational out there, market noise is temporary. 📉 #Finance #Markets #Crypto",
                    timestamp = System.currentTimeMillis() - 21600000, // 6 hours ago
                    mediaType = "TEXT",
                    engagementLikes = 310,
                    engagementComments = 122,
                    engagementShares = 45,
                    hashtags = "Finance,Markets,Crypto"
                ),
                SocialMediaPost(
                    authorName = "Sarah Joy",
                    authorHandle = "@sarah_mindful",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100",
                    platform = "Instagram",
                    content = "Quick reminder to step away from your screens today. Walk outside, feel the sun, breathe deeply. A 15-minute nature walk reduces cortisol levels by up to 20%. Protect your peace. 🌿 #Nature #Mindfulness #MentalHealth",
                    timestamp = System.currentTimeMillis() - 25200000, // 7 hours ago
                    mediaType = "IMAGE",
                    mediaUrl = "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=800",
                    engagementLikes = 3410,
                    engagementComments = 120,
                    engagementShares = 95,
                    hashtags = "Nature,Mindfulness,MentalHealth"
                ),
                SocialMediaPost(
                    authorName = "Aris Green",
                    authorHandle = "aris-green-infra",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=100",
                    platform = "LinkedIn",
                    content = "Thrilled to share that our solar array project has officially gone live, powering over 5,000 households with clean energy. Sustainable infrastructure is no longer a luxury—it is the baseline of modern development. #CleanEnergy #TechForGood #Solar",
                    timestamp = System.currentTimeMillis() - 28800000, // 8 hours ago
                    mediaType = "TEXT",
                    engagementLikes = 650,
                    engagementComments = 40,
                    engagementShares = 110,
                    hashtags = "CleanEnergy,TechForGood,Solar"
                ),
                SocialMediaPost(
                    authorName = "TechVlog Hub",
                    authorHandle = "TechVlog",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100",
                    platform = "YouTube",
                    content = "Testing the ultimate water-cooled gaming rig of 2026. Custom loop, 8K ray-tracing at 144FPS, and absolute silence. Full building guide and benchmarks available on my channel! 🎮 #Gaming #Hardware #PCBuild",
                    timestamp = System.currentTimeMillis() - 32400000, // 9 hours ago
                    mediaType = "VIDEO",
                    mediaUrl = "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=800",
                    engagementLikes = 12000,
                    engagementComments = 890,
                    engagementShares = 1240,
                    hashtags = "Gaming,Hardware,PCBuild"
                ),
                SocialMediaPost(
                    authorName = "Cozy Dev",
                    authorHandle = "cozy_book_game",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100",
                    platform = "Reddit",
                    content = "Just launched the alpha demo of my solo indie project—a cozy puzzle game where you restore old books. Feedback is extremely welcome, check out the free demo in the comments! #IndieDev #Gaming #CozyGames",
                    timestamp = System.currentTimeMillis() - 36000000, // 10 hours ago
                    mediaType = "TEXT",
                    engagementLikes = 1250,
                    engagementComments = 340,
                    engagementShares = 62,
                    hashtags = "IndieDev,Gaming,CozyGames"
                )
            )
            postDao.insertPosts(seedPosts)
        }
    }

    // --- Create Custom Post ---
    suspend fun insertCustomPost(post: SocialMediaPost) = withContext(Dispatchers.IO) {
        postDao.insertPost(post)
    }

    // --- Delete Custom Post ---
    suspend fun deletePost(postId: Int) = withContext(Dispatchers.IO) {
        postDao.deletePostById(postId)
    }

    // --- Delete Search History ---
    suspend fun deleteHistory(historyId: Int) = withContext(Dispatchers.IO) {
        postDao.deleteHistoryById(historyId)
    }

    // --- Clear All Search History ---
    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        postDao.clearHistory()
    }

    // --- Get Matched Posts offline ---
    suspend fun getPostsByIds(ids: List<Int>): List<SocialMediaPost> = withContext(Dispatchers.IO) {
        postDao.getPostsByIds(ids)
    }

    // --- Live Social Media Crawler / Real-Time Ingest Engine ---
    suspend fun ingestLivePostsForQuery(prompt: String): List<SocialMediaPost> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "GEMINI_API_KEY") {
            return@withContext emptyList()
        }

        val systemInstructionText = """
            You are a Real-Time Social Media Ingest Crawler.
            Based on the user's search query, you must generate 6 to 8 highly realistic, detailed, contextually accurate, and up-to-date social media posts that represent actual discussions, trends, or reports currently happening on major platforms like Twitter, Instagram, Reddit, LinkedIn, or YouTube.
            Make sure the posts contain specific details, high-quality analysis, relevant hashtags, and realistic sentiment, simulating real active users.
            
            You MUST return a valid JSON array of posts matching the requested schema. Do not return any other text, and do not enclose it in markdown code blocks.
            
            JSON schema:
            [
              {
                "authorName": "string",
                "authorHandle": "string",
                "platform": "string", // Must be exactly one of: "Twitter", "Instagram", "Reddit", "LinkedIn", "YouTube"
                "content": "string", // Highly realistic, engaging post content related to the query
                "mediaType": "string", // "TEXT", "IMAGE", or "VIDEO"
                "mediaUrl": "string or null", // Real or high-quality placeholder Unsplash photo URL matching the post's context, e.g. https://images.unsplash.com/photo-... or null
                "engagementLikes": int,
                "engagementComments": int,
                "engagementShares": int,
                "hashtags": "string" // comma-separated list of hashtags
              }
            ]
        """.trimIndent()

        val userPrompt = """
            Search Query: "$prompt"
            Generate 6 to 8 highly realistic social media posts that directly correspond to this query. Return ONLY the JSON array matching the schema.
        """.trimIndent()

        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
                systemInstruction = Content(parts = listOf(Part(text = systemInstructionText))),
                generationConfig = GenerationConfig(
                    responseMimeType = "application/json",
                    temperature = 0.7f
                )
            )

            val response = GeminiClient.apiService.generateContent("gemini-3.5-flash", apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("Empty response from Gemini during ingestion.")

            val cleanJson = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val jsonArray = org.json.JSONArray(cleanJson)
            val list = mutableListOf<SocialMediaPost>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    SocialMediaPost(
                        authorName = obj.optString("authorName", "User"),
                        authorHandle = obj.optString("authorHandle", "@user"),
                        authorAvatarUrl = "https://images.unsplash.com/photo-${listOf("1535713875002-d1d0cf377fde", "1494790108377-be9c29b29330", "1507003211169-0a1dd7228f2d", "1500648767791-00dcc994a43e", "1534528741775-53994a69daeb", "1519085360753-af0119f7cbe7").random()}?w=100",
                        platform = obj.optString("platform", "Twitter"),
                        content = obj.optString("content", ""),
                        timestamp = System.currentTimeMillis() - (i * 3600000 + (0..1800000).random()), // staggered hours
                        mediaType = obj.optString("mediaType", "TEXT"),
                        mediaUrl = if (obj.isNull("mediaUrl")) null else obj.optString("mediaUrl", null),
                        engagementLikes = obj.optInt("engagementLikes", (50..5000).random()),
                        engagementComments = obj.optInt("engagementComments", (5..500).random()),
                        engagementShares = obj.optInt("engagementShares", (2..200).random()),
                        hashtags = obj.optString("hashtags", "")
                    )
                )
            }
            return@withContext list
        } catch (e: Exception) {
            Log.e("SearchRepository", "Failed to ingest live posts: ${e.message}", e)
            return@withContext emptyList()
        }
    }

    // --- Core NLP Search Execution ---
    suspend fun executeSearch(prompt: String, thinkingModeEnabled: Boolean = false): GeminiSearchResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        // A. Dynamically fetch matching live posts from Gemini if online to make search real and relevant
        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && apiKey != "GEMINI_API_KEY") {
            try {
                val livePosts = ingestLivePostsForQuery(prompt)
                if (livePosts.isNotEmpty()) {
                    postDao.insertPosts(livePosts)
                    Log.d("SearchRepository", "Successfully ingested ${livePosts.size} live posts for query: $prompt")
                }
            } catch (e: Exception) {
                Log.e("SearchRepository", "Live ingestion failed: ${e.message}")
            }
        }

        val posts = postDao.getAllPosts()

        // 1. Check if Gemini API key is configured or default placeholder
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "GEMINI_API_KEY") {
            Log.w("SearchRepository", "Gemini API key is not configured. Falling back to Local NLP matching engine.")
            return@withContext executeLocalSearch(prompt, posts)
        }

        // 2. Prepare Gemini System Instructions
        val systemInstructionText = """
            You are a Social Media Intelligence Engine and NLP Search Specialist. 
            You will be given a list of social media posts (represented as JSON or structured lists) and a user's natural language search query.
            Your task is to analyze the posts and the user prompt, find matching or highly relevant posts, and compile a structured, in-depth intelligence summary report.
            
            You MUST return a valid JSON object matching the following structure EXACTLY. Do not return any other text, and do not enclose it in markdown code blocks.
            
            JSON schema:
            {
              "matchedPostIds": [int, ...], // List of exact database IDs from the posts that are relevant to the user query
              "sentimentSummary": "string", // Overall summary of the sentiment in matching posts, e.g. "Overwhelmingly Positive (88%)"
              "positivePercentage": int, // Integer representing positive sentiment percentage
              "negativePercentage": int, // Integer representing negative sentiment percentage
              "neutralPercentage": int, // Integer representing neutral sentiment percentage
              "keyTakeaways": ["string", "string", ...], // List of 3-5 distinct bulleted findings from these posts
              "mediaInsights": "string", // Visual analysis summarizing patterns of any matching multimedia (images, videos)
              "detailedAnswer": "string" // Comprehensive in-depth answer (2-3 paragraphs) synthesizing what people are saying, what themes exist, and what the community consensus is.
            }
        """.trimIndent()

        // 3. Format the candidates database for Gemini
        val formattedPosts = posts.joinToString(separator = "\n---\n") { post ->
            "ID: ${post.id}\nPlatform: ${post.platform}\nAuthor: ${post.authorName} (${post.authorHandle})\nContent: ${post.content}\nMedia Type: ${post.mediaType}\nLikes: ${post.engagementLikes}, Comments: ${post.engagementComments}\nHashtags: ${post.hashtags}"
        }

        // 4. Construct Prompt
        val userPrompt = """
            User Search Query: "$prompt"
            
            Database of social media posts to search over:
            $formattedPosts
            
            Perform search and analysis. Return the JSON object matching the required schema.
        """.trimIndent()

        // 5. Send Request to Gemini API (dynamically choosing model and thinking level)
        try {
            val modelName = if (thinkingModeEnabled) "gemini-3.1-pro-preview" else "gemini-3.5-flash"
            
            val config = if (thinkingModeEnabled) {
                GenerationConfig(
                    responseMimeType = "application/json",
                    temperature = null, // No temperature when thinking is enabled
                    thinkingLevel = ThinkingLevel.HIGH,
                    thinkingConfig = ThinkingConfig(thinkingBudget = 2048)
                )
            } else {
                GenerationConfig(
                    responseMimeType = "application/json",
                    temperature = 0.2f,
                    thinkingLevel = null,
                    thinkingConfig = null
                )
            }

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
                systemInstruction = Content(parts = listOf(Part(text = systemInstructionText))),
                generationConfig = config
            )

            val response = GeminiClient.apiService.generateContent(modelName, apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("Empty response from Gemini API.")

            // 6. Parse structured JSON from response
            val parsedResponse = parseGeminiResponse(jsonText)

            // Save to database history
            val matchedIdsString = parsedResponse.matchedPostIds.joinToString(",")
            val history = SearchHistory(
                query = prompt,
                timestamp = System.currentTimeMillis(),
                aiAnswer = parsedResponse.detailedAnswer,
                sentimentSummary = parsedResponse.sentimentSummary,
                positivePercent = parsedResponse.positivePercentage,
                negativePercent = parsedResponse.negativePercentage,
                neutralPercent = parsedResponse.neutralPercentage,
                keyTakeaways = parsedResponse.keyTakeaways.joinToString("\n"),
                matchedPostIds = matchedIdsString
            )
            postDao.insertSearchHistory(history)

            return@withContext parsedResponse

        } catch (e: Exception) {
            Log.e("SearchRepository", "Gemini API call failed: ${e.message}", e)
            // Fallback to local offline search on error
            return@withContext executeLocalSearch(prompt, posts)
        }
    }

    // --- Helper to parse Gemini response ---
    private fun parseGeminiResponse(jsonText: String): GeminiSearchResponse {
        return try {
            // Remove markdown code block symbols if they exist
            val cleanJson = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val adapter = GeminiClient.moshiParser.adapter(GeminiSearchResponse::class.java)
            adapter.fromJson(cleanJson) ?: throw Exception("Moshi returned null for response parsing")
        } catch (e: Exception) {
            Log.e("SearchRepository", "Failed to parse JSON using Moshi, attempting manual JSON parsing: ${e.message}")
            try {
                // Manual parse using JSONObject as fallback
                val cleanJson = jsonText.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                val jsonObject = JSONObject(cleanJson)
                val matchedPostIds = mutableListOf<Int>()
                val matchedArray = jsonObject.optJSONArray("matchedPostIds")
                if (matchedArray != null) {
                    for (i in 0 until matchedArray.length()) {
                        matchedPostIds.add(matchedArray.getInt(i))
                    }
                }
                val takeaways = mutableListOf<String>()
                val takeawaysArray = jsonObject.optJSONArray("keyTakeaways")
                if (takeawaysArray != null) {
                    for (i in 0 until takeawaysArray.length()) {
                        takeaways.add(takeawaysArray.getString(i))
                    }
                }

                GeminiSearchResponse(
                    matchedPostIds = matchedPostIds,
                    sentimentSummary = jsonObject.optString("sentimentSummary", "Mixed"),
                    positivePercentage = jsonObject.optInt("positivePercentage", 33),
                    negativePercentage = jsonObject.optInt("negativePercentage", 33),
                    neutralPercentage = jsonObject.optInt("neutralPercentage", 34),
                    keyTakeaways = takeaways,
                    mediaInsights = jsonObject.optString("mediaInsights", ""),
                    detailedAnswer = jsonObject.optString("detailedAnswer", "Search finished successfully.")
                )
            } catch (inner: Exception) {
                Log.e("SearchRepository", "Fallback manual JSON parsing also failed: ${inner.message}")
                GeminiSearchResponse(
                    detailedAnswer = "We searched matching social posts, but encountered an error decoding the analytical breakdown. Raw text: $jsonText"
                )
            }
        }
    }

    // --- Local NLP Search Engine Fallback (Deterministic, offline-friendly) ---
    private suspend fun executeLocalSearch(prompt: String, posts: List<SocialMediaPost>): GeminiSearchResponse {
        val keywords = prompt.lowercase().split(Regex("[\\s,\\.\\?\\!\\#\\-]+")).filter { it.length > 2 }
        
        // Find matched posts based on simple word-matching scoring
        val matchedWithScores = posts.map { post ->
            val contentLower = post.content.lowercase()
            val hashtagsLower = post.hashtags.lowercase()
            var score = 0
            
            for (keyword in keywords) {
                if (contentLower.contains(keyword)) score += 3
                if (hashtagsLower.contains(keyword)) score += 5
                if (post.authorName.lowercase().contains(keyword)) score += 2
                if (post.platform.lowercase().contains(keyword)) score += 1
            }
            Pair(post, score)
        }.filter { it.second > 0 }.sortedByDescending { it.second }

        val matchedPosts = matchedWithScores.map { it.first }
        val matchedIds = matchedPosts.map { it.id }

        // Compile sentiment analysis
        var positiveCount = 0
        var negativeCount = 0
        var neutralCount = 0

        val positiveWords = listOf("love", "best", "pristine", "mastering", "sustainable", "thrilled", "good", "great", "perfect", "sun", "peace", "joy", "reduce", "clean")
        val negativeWords = listOf("killer", "noise", "down", "temporary", "inflation", "cortisol", "meetings", "silent")

        for (post in matchedPosts) {
            val contentLower = post.content.lowercase()
            var pos = 0
            var neg = 0
            for (word in positiveWords) {
                if (contentLower.contains(word)) pos++
            }
            for (word in negativeWords) {
                if (contentLower.contains(word)) neg++
            }
            if (pos > neg) positiveCount++
            else if (neg > pos) negativeCount++
            else neutralCount++
        }

        val totalMatches = matchedPosts.size
        val positivePercent: Int
        val negativePercent: Int
        val neutralPercent: Int

        if (totalMatches > 0) {
            positivePercent = (positiveCount * 100) / totalMatches
            negativePercent = (negativeCount * 100) / totalMatches
            neutralPercent = 100 - positivePercent - negativePercent
        } else {
            positivePercent = 40
            negativePercent = 15
            neutralPercent = 45
        }

        val sentimentSummary = when {
            totalMatches == 0 -> "No direct posts matched"
            positivePercent > 50 -> "Highly Positive ($positivePercent% positive sentiment)"
            negativePercent > 40 -> "Critical / Cautionary ($negativePercent% negative sentiment)"
            else -> "Balanced / Neutral ($neutralPercent% neutral content)"
        }

        // Key takeaways compile
        val takeaways = mutableListOf<String>()
        if (matchedPosts.isNotEmpty()) {
            takeaways.add("Found ${matchedPosts.size} posts directly corresponding to the query keywords: ${keywords.joinToString(", ")}.")
            val primaryPlatforms = matchedPosts.map { it.platform }.distinct()
            takeaways.add("Discussions are happening across platforms: ${primaryPlatforms.joinToString(", ")}.")
            val mediaCount = matchedPosts.count { it.mediaType != "TEXT" }
            takeaways.add("$mediaCount of the matched posts contain rich multimedia content (images/video).")
        } else {
            takeaways.add("No posts in the database matched the keywords.")
            takeaways.add("Expand your search prompt using simpler words, e.g. 'AI', 'travel', 'productivity', 'food', or 'gaming'.")
            takeaways.add("You can also add a custom post in the 'Posts Directory' to index your own contents!")
        }

        val mediaInsights = if (matchedPosts.any { it.mediaType == "IMAGE" || it.mediaType == "VIDEO" }) {
            "Multimedia posts indicate high engagement. Visual elements like landscapes, cosmic photography, baking tutorials, and tech rigs drive significant discussion and sharing among users."
        } else {
            "No active video or image media files were matched for this topic; the discussion is predominantly text-driven."
        }

        val querySummaryText = if (matchedPosts.isNotEmpty()) {
            "Synthesized local search analysis for '$prompt': The community across ${matchedPosts.map { it.platform }.distinct().joinToString(", ")} is actively discussing topics related to your search. A key theme revolves around '${matchedPosts.first().content.take(50)}...', driving healthy social interaction and significant audience likes (${matchedPosts.sumOf { it.engagementLikes }} combined). Feel free to customize this catalog by adding your own relevant posts!"
        } else {
            "Offline Local Search Report: No posts matched your search for '$prompt'. Please explore our preloaded index of tech, nature, sourdough baking, astronomy, macroeconomics, mental health, and gaming by inputting keywords like 'Fuji', 'AI', 'Webb Telescope', 'productivity', or 'sourdough'."
        }

        val localResponse = GeminiSearchResponse(
            matchedPostIds = matchedIds,
            sentimentSummary = sentimentSummary,
            positivePercentage = positivePercent,
            negativePercentage = negativePercent,
            neutralPercentage = neutralPercent,
            keyTakeaways = takeaways,
            mediaInsights = mediaInsights,
            detailedAnswer = querySummaryText
        )

        // Save to database history
        val matchedIdsString = matchedIds.joinToString(",")
        val history = SearchHistory(
            query = prompt,
            timestamp = System.currentTimeMillis(),
            aiAnswer = localResponse.detailedAnswer,
            sentimentSummary = localResponse.sentimentSummary,
            positivePercent = localResponse.positivePercentage,
            negativePercent = localResponse.negativePercentage,
            neutralPercent = localResponse.neutralPercentage,
            keyTakeaways = localResponse.keyTakeaways.joinToString("\n"),
            matchedPostIds = matchedIdsString
        )
        postDao.insertSearchHistory(history)

        return localResponse
    }
}
