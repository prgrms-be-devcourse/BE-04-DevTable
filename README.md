## Dev-Table
> 캐치 테이블 클론 코딩

## 기술 스택

### Backend
<img src="https://img.shields.io/badge/JAVA 17-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/Spring 6.0.11-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/Spring Boot 3.1.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/Spring Security 6-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/REST DOCS 3.0.0-8CA1AF?style=for-the-badge&logo=restdocs&logoColor=black"> <img src="https://img.shields.io/badge/MySQL 8.0.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/H2 2.2.222-4479A1?style=for-the-badge&logo=h2&logoColor=white">  
<img src="https://img.shields.io/badge/JPA -59666C?style=for-the-badge&logo=hibernate&logoColor=white"> <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/Query DSL-6DB33F?style=for-the-badge&logo=querydsl&logoColor=white">   
<img src="https://img.shields.io/badge/Apache Tomcat-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black"> <img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=nginx&logoColor=white">

### Devops
<img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"> <img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"> <img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">

### Frontend
<img src="https://img.shields.io/badge/Create React App-61DAFB?style=for-the-badge&logo=createreactapp&logoColor=black"> <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black"> <img src="https://img.shields.io/badge/react router-CA4245?style=for-the-badge&logo=reactrouter&logoColor=black"> <img src="https://img.shields.io/badge/react query-FF4154?style=for-the-badge&logo=reactquery&logoColor=black"> <img src="https://img.shields.io/badge/tailwind css-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=black">

### Tools
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/Github Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"> <img src="https://img.shields.io/badge/Jira-0052CC?style=for-the-badge&logo=jira&logoColor=white"> 

## 📎 프로젝트 목적
> 프로젝트 기간 : 2023.08.29 ~ 2023.09.22  
> 캐치테이블을 클론 코딩하며 스프링과 JPA를 이용한 개발 능력 향상과 테이블 설계 능력을 향상한다.  
> 인프라 구축 및 협업 능력을 향상하고, 배포의 자동화를 적용해본다.  

## Contributors
### 백엔드
|Developer|Developer|Developer|Mentor|Mentor|
|:---------:|:---------:|:---------:|:------:|:------:|
|[김도연](https://github.com/joyfulviper)|[문종운](https://github.com/bombo-dev)|[한희나](https://github.com/heenahan)|[앨런](https://github.com/hongbin-dev)|[이태현](https://github.com/taehyunnn)|
|<img src='https://avatars.githubusercontent.com/u/79970349?v=4' width="200px">|<img src='https://avatars.githubusercontent.com/u/74203371?v=4' width="200px">|<img src='https://avatars.githubusercontent.com/u/83766322?v=4' width="200px">|<img src='https://avatars.githubusercontent.com/u/33685054?v=4' width="200px">|<img src='https://avatars.githubusercontent.com/u/53414145?v=4' width="200px">
## Update Record
### Sprint 1 
- 기간 : **[2023.09.01 ~ 2023.09.08]**
- **유저의 웨이팅 등록, 취소, 실시간 웨이팅 순서 확인 기능 구현**
- **점주의 방문 승인, 노쇼, 웨이팅 취소 기능 구현**
  - 이를 구현하기 위해 Redis 같은 이미 존재하는 캐시메모리를 사용하면 좋으나 직접 웨이팅을 처리하는 자료구조를 만들어서 구현
    - 방문 시간에 따라 매장 별로 웨이팅의 순서가 정해져야 하므로 `TreeMap<T, R>` 을 활용
    - T == 매장의 ID,
    - R == 웨이팅 정보가 담긴 객체

### Sprint 2
- 기간 : **[2023.09.10 ~ 2023.09.17]**
