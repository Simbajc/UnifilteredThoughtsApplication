CREATE DATABASE unfiltered_thoughts;

DESCRIBE accounts;
DESCRIBE journal_entries;

CREATE TABLE accounts (
    username VARCHAR(20) PRIMARY KEY NOT NULL UNIQUE,
    password VARCHAR(20) NOT NULL,
    creation_date DATETIME NOT NULL DEFAULT (NOW()),
    last_login_date DATE NOT NULL DEFAULT (CURDATE()),
    number_of_entries INT NOT NULL DEFAULT 0
);

CREATE TABLE journal_entries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_username VARCHAR(20) NOT NULL ,
    entry_number INT NOT NULL,
    content VARCHAR(2000) NOT NULL,
    FOREIGN KEY (account_username) REFERENCES accounts(username) ON DELETE CASCADE,
    UNIQUE(account_username, entry_number)
);

DROP TABLE journal_entries;

DELIMITER //
/*
automatically calulates what entry id for each user
*/
CREATE TRIGGER before_journal_insert
BEFORE INSERT ON journal_entries
FOR EACH ROW
BEGIN
    -- Calculate the next entry_number for the specific user
    SET NEW.entry_number = (
        SELECT COALESCE(MAX(entry_number), 0) + 1
        FROM journal_entries
        WHERE account_username = NEW.account_username
    );
END;

// DELIMITER ;

/*
-- TEST 
SELECT * FROM accounts;
SELECT * FROM journal_entries;
INSERT INTO accounts (username, password) VALUES ('user1', 'password1'), ('user2', 'password2');

INSERT INTO journal_entries (account_username, content) VALUES 
('user1', 'Second journal entry for user2'),
('user2', 'Third journal entry for user2');

DELETE FROM accounts WHERE username = 'user2';
*/


