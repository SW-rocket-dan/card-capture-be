## 단건 결제

![img.png](SinglePaymentV2.png)
- 포트원의 결제 API를 호출하려면 "포트원 API Secret"을 헤더에 넣거나, "포트원 API Access Token"을 헤더에 넣어야 합니다.
  - 해당 API들은 백엔드에서만 호출합니다.
  - API Secret은 만료기간이 더 깁니다. 
  - 어차피 백엔드에서는 secret알고있는 상황이고, 이 access token이 필요한 경우에는, secret을 모르는 사람에게 짧은 시간동안 권한을 빌려주기 위함이다. 
   즉, 우리 입장에서는 필요가 없으므로, "포트원 API Secret"을 사용하도록 합니다.

- ~~포트원 API Access Token 및 Refresh Token 발급받는 방법~~ 
  - ~~최신 정보는 https://developers.portone.io/api/rest-v2/auth 참고~~
~~~
curl --request post \
  --url https://api.portone.io/login/api-secret \
  --header 'Content-Type: application/json' \
  --data '{"apiSecret":"your-api-secret"}'
~~~

- 백엔드에서 포트원 API 호출 시 RestClient를 사용합니다.

### [1~3]
- 고객이 "구매하기" 버튼을 눌렀을 때, 프론트에서는 백엔드에 다음을 요청합니다.
  구매가 가능한 상황을 확인하고 응답을 보내줘야 합니다.
  - 구매 가능한지 요청 시 포맷
~~~
{
  "paymentId": "example-payment-id", // 프론트에서 설정해주는 "결제의 고유 번호"
  "products": [
    {
      "productId": "example-product-id", // 상품의 고유 번호
      "quantity": 1, // 상품의 수량
      "price": 1000 // 상품의 가격
    },
    {
      "productId": "example-product-id-2",
      "quantity": 2,
      "price": 2000
    }
  ],
  "totalAmount": 3000,
  "timestamp": "2024-04-25T10:00:00.000Z",
}
~~~

- 구매 불가능한 상황이란?
  - 재고가 있는 상품: 재고가 0개일 때
  - 모든 상품: 이 상품을 팔면, 프로젝트 전체의 판매액이 762만원을 넘어가는 구매일 때

- ⭐️동시성 처리
  - 같은 "구매 요청건"에 대해 똑같은 요청이 동시에 여러 개 도착해도, 한 번만 "구매가능하다"라고 보내줘야 한다.
    - 분산 서버 환경에서도 동작해야 한다.
    - paymentId로 중복 요청을 구분한다.
    - 


### [11]
- 프론트에서 포트원과 결제를 진행하면, 포트원은 결제 결과를 백엔드에 "웹훅"으로 알려줍니다
  - "결제 결과"에 대해 웹훅을 받을 엔드포인트가 필요합니다.
  - "결제 결과"를 검증하고, DB에 저장합니다.
  - 웹훅은 신뢰할 수 없기 때문에, 아래와 같은 방법으로 "결제 결과"를 검증합니다. (출처: 포트원)
~~~
    웹훅 수신 주소는 공개된 URL이기 때문에, 수신한 웹훅 메시지의 내용을 신뢰할 수 없습니다.
    예를 들어, 결제 완료 알림을 웹훅으로 수신했다고 해서 꼭 결제가 완료된 것이 아닐 수 있습니다.
  - 웹훅 메시지를 처리하는 전략은 두 가지가 있습니다. 
  1)웹훅 메시지를 신뢰하지 않고, 결제 건의 상태를 포트원 API를 통해 새로 조회하여 이 응답만 신뢰하기
  2)웹훅 메시지를 검증하기
https://github.com/standard-webhooks/standard-webhooks/blob/main/spec/standard-webhooks.md
~~~
=> 1번으로 진행 (웹훅이 도착하면 결제가 일어났다는 알람으로 받아들이고, 포트원 API로 결제 결과 검증)
- 포트원 API를 통해 단건 결제 상태를 조회할 수 있다.
  - 모든 조회 결과는 DB에 저장한다. 
  - 포트원의 결제 상태는 다음과 같다.
  - ![img_1.png](PortonePaymentStatus.png)
  - 포트원 API로 결제 상태 "조회 실패(포트원 측에서 4xx, 5xx 보내면)" => "조회 불가"라고 프론트에 알린다.  
- 포트원 API를 통해 결제를 취소시킬 수 있다.


- 단건 결제 상태를 조회하는 API를 제공한다.
  - 프론트에서 결제를 해도 되는지 확인하기 위해 사용한다.
  - 회원 정보, 결제 상품, 결제 금액

- (1일 1회 배치) 배치 작업을 통해, 전날(00:00:00~23:59:59)의 포트원의 결제 내역을 조회하여, DB와 다른 내용이 있다면 
  - 결제 내역 DB를 업데이트하고,
  - 이상 결제 내역 DB에 저장하고,
  - 관리자에게 알림을 보낸다.


~~~
https://api.portone.io/payments?requestBody=인코딩된_요청_바디
예) 인코딩_전_요청_바디={"page":{"number":2,"size":5},"filter":{"isTest":false,"from":"2024-07-25T00:00:00Z","until":"2024-07-25T23:59:59Z"}}
~~~
