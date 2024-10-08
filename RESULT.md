### 해당 프로젝트에서 발생할 수 있는 위험 요소

---
1. 로그인을 한 인증값을 id로 부여 하면 보안 이슈가 생길수 있음 
2. 페이백 금액이 원 승인 금액의 특정 비율로 들어올 경우, 소수점 처리를 어떻게 해야 할지 논의가 필요 
3. 결제 데이터가 많아질 경우, 검색 성능이 안나올수 있음
4. (통화, 승인금액, 승인번호)가 완벽하게 유니크 하다고 볼수 없음 
5. 유저의 일별, 월별, 연별 누적 승인금액을 계산 하는 경우


<br/>

### 위험 요소 해결하는 방법 혹은 방향

---

1. Session 혹은 Token 기반의 보안을 추가해야 할것 같다. 
   1. 대표적으로 Spring Security와 JWT를 활용한 Token 기반 방식이 있다.
2. 정책적인 부분이지만, 회사의 이득을 위해 내림 처리를 하는 방법도 있다.
3. 검색 성능은 인덱스를 추가하면 된다. 
   1. 단 인덱스를 만들때는 검색 조건에서 가장 많이 쓰이면서, 같이 사용되는 형태의 복합 인덱스를 만드는 것이 좋을 것 같다.
4. 승인번호를 숫자 6자리로만 제한을 해뒀기에, 유저가 반복적인 금액으로 결제를 시도 한다면 어느 순간 유니크함이 보장되지 않을수 있다. 
   1. 그래서, 생성 날짜 혹은 UUID를 이용해서 매핑을 추가하면
   2. 산술적으로 유니크 함이 거의 보장될 것이라고 생각이 들었다.
5. 매번, authorizations와 user를 join해서 누적 금액을 계산 하는 것은 비효율적일수 있습니다.
   1. 관련 Table을 추가로 신설하는 것이 가장 좋을 것 같습니다.
      1. cache or redis 등을 사용할수 있지만, 사용자가 많아지고 결제량이 늘수록 메모리에 많이 올라가게 되어 서버에 부담을 줄수 있다고 생각합니다. 


