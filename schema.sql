CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(120) NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    phone       VARCHAR(15),
    role        VARCHAR(10)  NOT NULL DEFAULT 'MEMBER'
);

CREATE TABLE IF NOT EXISTS books (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn              VARCHAR(20)  NOT NULL UNIQUE,
    title             VARCHAR(200) NOT NULL,
    author            VARCHAR(150) NOT NULL,
    category          VARCHAR(60),
    total_copies      INT NOT NULL,
    available_copies  INT NOT NULL
);

CREATE TABLE IF NOT EXISTS issues (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id      BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    issue_date   DATE   NOT NULL,
    due_date     DATE   NOT NULL,
    return_date  DATE,
    fine         INT    NOT NULL DEFAULT 0,
    status       VARCHAR(10) NOT NULL DEFAULT 'ISSUED',
    INDEX idx_issues_status (status),
    CONSTRAINT fk_issue_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT fk_issue_user FOREIGN KEY (user_id) REFERENCES users(id)
);
