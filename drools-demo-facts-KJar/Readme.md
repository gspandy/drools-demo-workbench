1.往kie server 生成container，并启动容器
put http://localhost:8082/kie-server/services/rest/server/containers/drools-demo-facts-KJar

request body: 
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<kie-container>
    <config-items>
        <itemName>RuntimeStrategy</itemName>
        <itemValue>SINGLETON</itemValue>
        <itemType>java.lang.String</itemType>
    </config-items>
    <config-items>
        <itemName>MergeMode</itemName>
        <itemValue>MERGE_COLLECTIONS</itemValue>
        <itemType>java.lang.String</itemType>
    </config-items>
    <config-items>
        <itemName>KBase</itemName>
        <itemValue></itemValue>
        <itemType>java.lang.String</itemType>
    </config-items>
    <config-items>
        <itemName>KSession</itemName>
        <itemValue></itemValue>
        <itemType>java.lang.String</itemType>
    </config-items>
    <!-- This is where you define your kjar as an artifactid so that 
         the kie-server can find and deploy it. -->
    <release-id>
        <artifact-id>drools-demo-facts-KJar</artifact-id>
        <group-id>com.drools.facts</group-id>
        <version>1.0.0-SNAPSHOT</version>
    </release-id>
    <scanner poll-interval="5000" status="STARTED"/>
</kie-container>



POST http://localhost:8082/kie-server/services/rest/server/containers/instances/drools-demo-facts-KJar HTTP/1.1
content-type: application/json
accept: application/json
X-KIE-ContentType: JSON
Accept: application/json
Authorization: Basic [your authorization]
Cache-Control: no-cache

{
    "lookup": "defaultStatelessKieSession",
    "commands": [
        {
            "set-global": {
                "identifier": "courses",
                "object": {
                    "com.drools.facts.CourseList": {
                        "items": null
                    }
                },
                "out-identifier": "courses"
            }
        },
        {
            "insert": {
                "object": {
                    "com.drools.facts.Subject": {
                        "subject": "math",
                        "rating": 15
                    }
                },
                "out-identifier": null,
                "return-object": true,
                "entry-point": "DEFAULT",
                "disconnected": false
            }
        },
        {
            "insert": {
                "object": {
                    "com.drools.facts.Subject": {
                        "subject": "physics",
                        "rating": 15
                    }
                },
                "out-identifier": null,
                "return-object": true,
                "entry-point": "DEFAULT",
                "disconnected": false
            }
        },
        {
            "fire-all-rules": {
                "max": -1,
                "out-identifier": null
            }
        }
    ]
}



response body

{
    "type": "SUCCESS",
    "msg": "Container drools-demo-facts-KJar successfully called.",
    "result": {
        "execution-results": {
            "results": [
                {
                    "value": {
                        "com.drools.facts.CourseList": {
                            "items": [
                                {
                                    "course": "THEOR-PHYS"
                                }
                            ]
                        }
                    },
                    "key": "courses"
                }
            ],
            "facts": []
        }
    }
}