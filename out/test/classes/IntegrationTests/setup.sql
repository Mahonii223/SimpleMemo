create table memos(
    id          INT unsigned NOT NULL AUTO_INCREMENT,
    title       VARCHAR(150) NOT NULL,
    content     VARCHAR(3000) NOT NULL,
    version     INT unsigned NOT NULL,
    deleted     BOOL DEFAULT 0,
    created     DATE NOT NULL,
    modified    DATE NOT NULL,
    threadid    INT unsigned,
    PRIMARY KEY (id)
)