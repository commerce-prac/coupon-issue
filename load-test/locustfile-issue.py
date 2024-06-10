import random
from locust import task, FastHttpUser

class CouponIssue(FastHttpUser):
    connection_timeout = 10.0
    network_timeout = 10.0

    @task
    def hello(self):
        payload = {
            "userId" : random.randint(1, 10000000),
            "couponId" : 1,
        }
        with self.rest("POST", "/v1/issue", json=payload):
            pass