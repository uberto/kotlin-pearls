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



class DoSomething: ApiClient by HttpApiClient(){
    //other stuff needed to do something
}

class DoSomethingTest(): ApiClient by ClientMock {
    //other stuff needed to do something
}

class DoSomethingDelegated( client: ApiClient): ApiClient by client {
    //other stuff needed to do something
}

class DoSomethingOverriden( client: ApiClient): ApiClient by client {
    //other stuff needed to do something

    override fun callApi(request: String): String {
        return "override $request"
    }
}


class DoSomethingOnFly( client: ApiClient): ApiClient by object : ApiClient {
    override fun callApi(request: String): String {


        client.callApi(request)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
} {
    //other stuff needed to do something
}


class DoSomethingDoubleBy( client: ApiClient): ApiClient by object : ApiClient by client {
    override fun callApi(request: String): String {
        println("DoSomethingDoubleBy: hello!")
        return client.callApi(request)
    }
}

class DoSomethingWrapper(val client: ApiClient): ApiClient by client {

    override fun callApi(request: String): String {
        println("DoSomethingWrapper: hello!")
        return client.callApi(request)
    }
}


