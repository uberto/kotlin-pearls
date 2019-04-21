package com.ubertob.implementationDelegation



interface ApiClient {
    fun callApi(request: String): String
}

object ClientMock: ApiClient {
    override fun callApi(request: String): String = "42"
}


class HttpApiClient: ApiClient {
    override fun callApi(request: String): String {
        //... lot of code
        return "The Answer to $request is 42"
    }

}


fun repeatCall(client: ApiClient, times: Int) =
        (1..times)
            .map{client.callApi("request $it")}



class DoSomething: ApiClient by HttpApiClient() {

}

class DoSomethingWrapper(client: ApiClient): ApiClient by client {

}

class DoSomethingTest(): ApiClient by ClientMock {

}
