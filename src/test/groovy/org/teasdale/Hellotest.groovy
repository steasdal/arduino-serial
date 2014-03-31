package org.teasdale

import spock.lang.Specification

class Hellotest extends Specification {
    def "test getHello method"() {
        expect: "getHello method return the expected string"
        Hello.getHello() == "Hello, Groovy!"
    }
}
