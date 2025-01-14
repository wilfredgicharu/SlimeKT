/*
 * Copyright (C) 2021, Kasem S.M
 * All rights reserved.
 */
package kasem.sm.topic.datasource_impl.network

import com.slime.auth_api.AuthManager
import com.slime.auth_api.ID
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import kasem.sm.core.utils.userIdParam
import kasem.sm.core.utils.withResult
import kasem.sm.topic.datasource.network.TopicApiService
import kasem.sm.topic.datasource.network.response.SlimeResponse
import kasem.sm.topic.datasource.network.response.TopicResponse

internal class TopicApiServiceImpl @Inject constructor(
    private val client: HttpClient,
    private val authManager: AuthManager
) : TopicApiService {
    override suspend fun getAllTopics(): Result<SlimeResponse<List<TopicResponse>>> {
        return withResult {
            client.get(GET_ALL_TOPICS_ROUTE)
        }
    }

    override suspend fun getTopicById(id: String): Result<SlimeResponse<TopicResponse>> {
        return withResult {
            client.get(GET_TOPIC_BY_ID) {
                userIdParam(id = authManager.getUserData(ID))
                parameter("id", id)
            }
        }
    }

    override suspend fun getSubscribedTopics(): Result<SlimeResponse<List<TopicResponse>>> {
        return withResult {
            client.get(GET_SUBSCRIBED_TOPICS_ROUTE) {
                userIdParam(id = authManager.getUserData(ID))
            }
        }
    }

    override suspend fun getExploreTopics(): Result<SlimeResponse<List<TopicResponse>>> {
        return withResult {
            client.get(GET_EXPLORE_TOPICS_ROUTE) {
                userIdParam(id = authManager.getUserData(ID))
            }
        }
    }

    override suspend fun subscribeIfNot(id: String): Result<SlimeResponse<String>> {
        return withResult {
            client.post(SUBSCRIBE_IF_NOT) {
                contentType(ContentType.Application.Json)
                parameter("topicId", id)
            }
        }
    }

    companion object EndPoints {
        const val GET_ALL_TOPICS_ROUTE = "/api/topic/all"
        const val GET_TOPIC_BY_ID = "/api/topic/get"
        const val GET_SUBSCRIBED_TOPICS_ROUTE = "/api/subscriptionService/all"
        const val SUBSCRIBE_IF_NOT = "/api/subscriptionService/subscribeIfNot"
        const val GET_EXPLORE_TOPICS_ROUTE = "/api/subscriptionService/explore"
    }
}
