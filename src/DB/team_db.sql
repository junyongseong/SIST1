-- DB 재생성
DROP DATABASE IF EXISTS team_db;
CREATE DATABASE IF NOT EXISTS team_db;
USE team_db;

-- 부서 테이블
DROP TABLE IF EXISTS dept;
CREATE TABLE dept (
  deptno INT(2) NOT NULL,
  dname VARCHAR(50) NOT NULL,
  CONSTRAINT dept_pk PRIMARY KEY(deptno)
);

-- 역할 테이블 (1 : 일반직원, 2 : 관리자)
DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
  role_num INT PRIMARY KEY,
  role_name VARCHAR(30) NOT NULL
);

-- 사원 테이블
DROP TABLE IF EXISTS emp;
CREATE TABLE emp (
  empno BIGINT AUTO_INCREMENT NOT NULL, -- 사번
  deptno INT NOT NULL,
  role_num INT, -- 역할 구분
  ename VARCHAR(50) NOT NULL,
  work_status TINYINT NOT NULL, -- 0: 재직, 1: 퇴직 등
  sal DECIMAL(10,2) NOT NULL,
  hireDATE DATE NOT NULL,
  resign_DATE DATE,
  posname varchar(30),
  phone VARCHAR(20) NOT NULL,
  email VARCHAR(50),
  mgr BIGINT NULL, -- 상사의 사번 (nullable)
  username VARCHAR(50) NOT NULL,
  password VARCHAR(50) NOT NULL,

  -- 제약 조건
  CONSTRAINT emp_pk PRIMARY KEY(empno),
  CONSTRAINT emp_dept_fk FOREIGN KEY(deptno) REFERENCES dept(deptno),
  CONSTRAINT emp_mgr_fk FOREIGN KEY(mgr) REFERENCES emp(empno),
  CONSTRAINT emp_role_fk FOREIGN KEY(role_num) REFERENCES roles(role_num),
  CONSTRAINT emp_username_uq UNIQUE(username),
  CONSTRAINT emp_email_uq UNIQUE(email)
);

-- 문서 작성 테이블
DROP TABLE IF EXISTS documents;
CREATE TABLE documents (
  docs_num BIGINT AUTO_INCREMENT NOT NULL,
  empno BIGINT NOT NULL,
  deptno INT NOT NULL,
  title VARCHAR(50) NOT NULL,
  visibility ENUM('all', 'dept') DEFAULT 'dept',
  logs_num INT,
  content TEXT,
  date DATE NOT NULL,

  CONSTRAINT doc_pk PRIMARY KEY(docs_num),
  CONSTRAINT doc_emp_fk FOREIGN KEY(empno) REFERENCES emp(empno),
  CONSTRAINT doc_dept_fk FOREIGN KEY(deptno) REFERENCES dept(deptno)
);

-- 근태 테이블
DROP TABLE IF EXISTS attendance;
CREATE TABLE attendance (
  attendno BIGINT AUTO_INCREMENT,
  empno BIGINT NOT NULL,
  date DATE NOT NULL,
  chkin TIME,
  chkout TIME,
  attend_status TINYINT NOT NULL, --  0: 휴가(즉 연차) 1: 오전 반차 2: 오후 반차 
  attend_note TEXT,

  CONSTRAINT attendance_pk PRIMARY KEY(attendno),
  CONSTRAINT attendance_fk FOREIGN KEY(empno) REFERENCES emp(empno)
);
-- 휴가 테이블
CREATE TABLE leave_of(
  lnum INT AUTO_INCREMENT,
  empno BIGINT NOT NULL,
  lname VARCHAR(50) NOT NULL,
  ldate DATE NOT NULL,
  duration DECIMAL(5,2) NOT NULL,
  lstatus TINYINT NOT NULL,  -- 0: 대기, 1: 승인, 2: 반려 등
  lprocessed DATE,
  CONSTRAINT leave_of_pk PRIMARY KEY(lnum),
  CONSTRAINT FOREIGN KEY (empno) REFERENCES emp(empno)
);

-- 휴가 기록 테이블
CREATE TABLE leave_history (
  idx INT AUTO_INCREMENT,
  empno BIGINT NOT NULL,
  total_leave DECIMAL(4,1) NOT NULL,
  year INT NOT NULL,
  remain_leave DECIMAL(4,1) NOT NULL,
  CONSTRAINT leave_policy PRIMARY KEY(idx),
  CONSTRAINT FOREIGN KEY (empno) REFERENCES emp(empno)
);

-- 문서 공유를 위한 테이블
CREATE TABLE docs_shared(
   share_id INT AUTO_INCREMENT,
    docs_num BIGINT,
    deptno INT,
    
    CONSTRAINT docs_shared_pk PRIMARY KEY(share_id),
    CONSTRAINT FOREIGN KEY (docs_num) REFERENCES documents(docs_num),
    CONSTRAINT FOREIGN KEY (deptno) REFERENCES dept(deptno)
);

-- 기본 데이터 삽입
-- 부서 데이터
INSERT INTO dept (deptno, dname) VALUES
(10, '개발부'),
(20, '인사부'),
(30, '영업부'),
(40, '관리부'),
(50, '총무부');


-- 역할 데이터
INSERT INTO roles (role_num, role_name) VALUES
(1, '일반직원'),
(2, '관리자'),
(3, '사장');

-- 사원 데이터 (posno 제거됨)
INSERT INTO emp (empno, deptno, role_num, ename,posname, work_status, sal, hiredate, resign_date, phone, email, mgr, username, password)
VALUES
(1000, 10, 3, '성용준','사장', 0, 10000.00, '2022-01-01', NULL, '010-1111-1111', 'yongjun@naber.com', NULL, '0000', '0000'),
(1001, 20, 3, '김인사','팀장', 0, 5800.00, '2023-02-01', NULL, '010-1111-0002', 'admin2@naber.com', NULL, 'admin', 'adminpw'), -- 인사부 팀장과 사장만 권한 3

(1002, 10, 2, '김개발','팀장', 0, 6000.00, '2023-01-01', NULL, '010-1111-0001', 'admin1@naber.com', NULL, '1111', '1111'), -- 각 부서 팀장들은 권한 2
(1003, 30, 2, '김영업','팀장', 0, 6000.00, '2023-01-01', NULL, '010-1111-0001', 'sales@naber.com', NULL, '2222', '2222'),
(1004, 40, 2, '김관리','팀장', 0, 6000.00, '2023-01-01', NULL, '010-1111-0001', 'man@naber.com', NULL, '3333', '3333'),
(1005, 50, 2, '김총무','팀장', 0, 6000.00, '2023-01-01', NULL, '010-1111-0001', 'sec@gooole.com', NULL, '4444', '4444'),

(1006, 10, 1, '박개발','대리', 0, 3000.00, '2023-03-01', NULL, '010-2222-0003', 'dev1@gooole.com', 1002, '5555', '5555'),
(1007, 10, 1, '최개발','사원', 0, 3100.00, '2023-04-01', NULL, '010-2222-0004', 'dev2@gooole.com', 1002, 'dev2', 'pass2'),
(1008, 10, 1, '정개발','사원', 0, 3500.00, '2023-05-01', NULL, '010-2222-0005', 'dev3@naber.com', 1002, 'dev3', 'pass3'),

(1009, 20, 1, '이인사','대리', 0, 2800.00, '2023-03-15', NULL, '010-3333-0008', 'hr1@naber.com', 1001, 'hr1', 'pass6'),
(1010, 20, 1, '구인사','사원', 0, 3200.00, '2023-04-15', NULL, '010-3333-0009', 'hr2@naber.com', 1001, 'hr2', 'pass7'),
(1011, 20, 1, '박인사','사원', 0, 2700.00, '2023-05-15', NULL, '010-3333-0010', 'hr3@gooole.com', 1001, 'hr3', 'pass8'),

(1012, 30, 1, '조영업','대리', 0, 2900.00, '2023-02-01', NULL, '010-4444-0011', 'sales1@gooole.com', 1003, 'sales1', 'pass9'),
(1013, 30, 1, '하영업','사원', 0, 3500.00, '2023-03-01', NULL, '010-4444-0012', 'sales2@gooole.com', 1003, 'sales2', 'pass10'),
(1014, 30, 1, '노영업','사원', 0, 2800.00, '2023-04-01', NULL, '010-4444-0013', 'sales3@gooole.com', 1003, 'sales3', 'pass12'),

(1015, 40, 1, '조관리','대리', 0, 2900.00, '2022-02-05', NULL, '010-5555-0012', 'man1@naber.com', 1004, 'man1', 'pass15'),
(1016, 40, 1, '하관리','사원', 0, 3500.00, '2023-05-01', NULL, '010-5555-0013', 'man2@naber.com', 1004, 'man2', 'pass18'),
(1017, 40, 1, '노관리','사원', 0, 2800.00, '2023-07-02', NULL, '010-5555-0014', 'man3@naber.com', 1004, 'man3', 'pass12'),

(1018, 50, 1, '조총무','대리', 0, 2900.00, '2022-04-03', NULL, '010-6666-0012', 'sec1@naber.com', 1005, 'sec1', 'pass13'),
(1019, 50, 1, '하총무','사원', 0, 3500.00, '2024-03-21', NULL, '010-6666-0013', 'sec2@gooole.com', 1005, 'sec2', 'pass11'),
(1020, 50, 1, '노총무','사원', 0, 2800.00, '2025-04-01', NULL, '010-6666-0014', 'sec3@gooole.com', 1005, 'sec3', 'pass12'),

-- 퇴사자 확인용으로 만듬
(1021, 10, 1, '임개발','대리', 1, 3100.00, '2023-07-01', '2024-01-12', '010-2222-0017', 'dev6@gooole.com', 1001, 'dev4', 'pass13'),
(1022, 20, 1, '신인사','사원', 1, 2900.00, '2023-07-15', '2025-02-15', '010-3333-0018', 'hr5@gooole.com', 1002, 'hr4', 'pass14'),
(1023, 30, 1, '백영업','대리', 1, 2750.00, '2023-07-20', '2025-06-12', '010-4444-0019', 'sales6@naber.com', 1003, 'sales4', 'pass15');

-- 샘플 문서 데이터
INSERT INTO documents (docs_num, empno, deptno, title, visibility, logs_num, content, date)
VALUES
(1, 1003, 10, '개발일지', 'dept', NULL, '오늘은 로그인 기능을 구현.', '2025-06-17'), -- 본인 부서만
(2, 1004, 10, '회의록', 'all', NULL, '개발 회의 내용 정리', '2025-06-17'), -- 부서 전체에 보내는거
(3, 1008, 20, '인사 회의자료', 'dept', NULL, '채용 관련 검토안', '2025-06-18');

-- 샘플 휴가 데이터
INSERT INTO leave_of (lnum, empno, lname, ldate, duration, lstatus, lprocessed)
VALUES
(124, 1000, '오후 반차', '2025-07-02', 0.5, 2, '2025-07-01'),
(125, 1001, '가족행사', '2025-07-03', 4.0, 0, NULL),
(126, 1001, '개인 사유 휴가', '2025-07-04', 2.0, 2, '2025-07-03'),
(127, 1002, '오전 반차', '2025-07-05', 0.5, 0, NULL),
(128, 1002, '오전 반차', '2025-07-06', 0.5, 2, '2025-07-05'),
(129, 1003, '가족행사', '2025-07-07', 4.0, 0, NULL),
(130, 1003, '경조사', '2025-07-08', 2.0, 2, '2025-07-07'),
(131, 1004, '가족행사', '2025-07-09', 1.0, 0, NULL),
(132, 1004, '가족행사', '2025-07-10', 2.0, 2, '2025-07-09'),
(133, 1005, '가족행사', '2025-07-11', 4.0, 0, NULL),
(134, 1005, '오후 반차', '2025-07-12', 0.5, 2, '2025-07-11'),
(135, 1006, '개인 사유 휴가', '2025-07-13', 4.0, 0, NULL),
(136, 1006, '경조사', '2025-07-14', 1.0, 2, '2025-07-13'),
(137, 1007, '오후 반차', '2025-07-15', 0.5, 0, NULL),
(138, 1007, '개인 사유 휴가', '2025-07-16', 4.0, 2, '2025-07-15'),
(139, 1008, '오후 반차', '2025-07-17', 0.5, 0, NULL),
(140, 1008, '경조사', '2025-07-18', 3.0, 2, '2025-07-17'),
(141, 1009, '개인 사유 휴가', '2025-07-19', 1.0, 0, NULL),
(142, 1009, '경조사', '2025-07-20', 1.0, 2, '2025-07-19'),
(143, 1010, '개인 사유 휴가', '2025-07-21', 2.0, 0, NULL),
(144, 1010, '개인 사유 휴가', '2025-07-22', 2.0, 2, '2025-07-21'),
(145, 1011, '경조사', '2025-07-23', 4.0, 0, NULL),
(146, 1011, '경조사', '2025-07-24', 4.0, 2, '2025-07-23'),
(147, 1012, '개인 사유 휴가', '2025-07-25', 2.0, 0, NULL),
(148, 1012, '오후 반차', '2025-07-26', 0.5, 2, '2025-07-25'),
(149, 1013, '경조사', '2025-07-27', 3.0, 0, NULL),
(150, 1013, '가족행사', '2025-07-28', 3.0, 2, '2025-07-27'),
(151, 1014, '가족행사', '2025-07-29', 2.0, 0, NULL),
(152, 1014, '경조사', '2025-07-30', 3.0, 2, '2025-07-29');

-- 샘플 연차 정책
INSERT INTO leave_history (idx, empno, total_leave, year, remain_leave)
VALUES
(1, 1000, 10.0, 2024, 8.0),
(2, 1000, 13.0, 2025, 9.5),
(3, 1001, 7.0, 2024, 6.5),
(4, 1001, 10.0, 2025, 9.5),
(5, 1002, 7.0, 2024, 5.0),
(6, 1002, 10.0, 2025, 3.0),
(7, 1003, 7.0, 2024, 4.0),
(8, 1003, 10.0, 2025, 5.5),
(9, 1004, 7.0, 2024, 0.0),
(10, 1004, 10.0, 2025, 5.5),
(11, 1005, 7.0, 2024, 7.0),
(12, 1005, 10.0, 2025, 8.5),
(13, 1006, 7.0, 2024, 3.5),
(14, 1006, 10.0, 2025, 8.0),
(15, 1007, 7.0, 2024, 7.0),
(16, 1007, 10.0, 2025, 0.0),
(17, 1008, 7.0, 2024, 5.5),
(18, 1008, 10.0, 2025, 8.5),
(19, 1009, 7.0, 2024, 1.5),
(20, 1009, 10.0, 2025, 0.5),
(21, 1010, 7.0, 2024, 4.0),
(22, 1010, 10.0, 2025, 3.0),
(23, 1011, 7.0, 2024, 3.5),
(24, 1011, 10.0, 2025, 5.5),
(25, 1012, 7.0, 2024, 4.0),
(26, 1012, 10.0, 2025, 4.5),
(27, 1013, 7.0, 2024, 3.5),
(28, 1013, 10.0, 2025, 3.0),
(29, 1014, 7.0, 2024, 6.5),
(30, 1014, 10.0, 2025, 3.0);

INSERT INTO docs_shared(share_id, docs_num,deptno)
VALUES
(1,2,20);

-- 0: 출근 1퇴근 2: 휴가(즉 연차) 3: 오전 반차 4: 오후 반차
INSERT INTO attendance (attendno, empno, date, chkin, chkout, attend_status, attend_note)
VALUES
(1, 1003, '2025-06-17', '09:00:00', '18:00:00', 0, '정상 출근'),
(2, 1004, '2025-06-17', '09:10:00', '18:00:00', 0, '10분 지각'),
(3, 1005, '2025-06-17', '00:00:00', '00:00:00', 2, '연차 or휴가'),
(4, 1006, '2025-06-17', '09:00:00', '17:30:00', 1, '조퇴 처리'),
(5, 1003, '2025-06-18', '08:55:00', '13:00:00', 4, '오후 반차'),
(6, 1004, '2025-06-18', '13:00:00', '18:30:00', 3, '오전 반차');