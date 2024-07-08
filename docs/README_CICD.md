## [배포/CI/CD]

### 배포
1. 소마 AWS 계정으로 EC2 생성
2. 탄력적 IP 구매
3. 서브 도메인 api.cardcapture.app으로 포트 연결(가비아 및 nginx 설정)

***
4. MySQL 배포 => 자동화할 수 있는지?
5. Github actions를 이용한 CI/CD
- MySQL 환경에서도 테스트 가능 : GitHub Actions 워크플로우 파일에서 services 섹션을 사용하여 MySQL 컨테이너를 정의하면, GitHub Actions가 해당 컨테이너를 자동으로 시작하고 관리합니다. 따라서 MySQL 컨테이너를 별도로 올릴 필요가 없습니다.
- 빌드 파일을 s3에 올려야하나?
- codedeploy를 써야만 하나?
- CI/CD 과정 flow 그려보기
