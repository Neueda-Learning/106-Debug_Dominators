CREATE TABLE IF NOT EXISTS payment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    payment_id VARCHAR(64) NOT NULL,
    reference_number VARCHAR(64) NOT NULL,
    source_account VARCHAR(255) NOT NULL,
    destination_account VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 8) NOT NULL,
    currency VARCHAR(16) NOT NULL,
    payment_method VARCHAR(32) NOT NULL,
    source_country VARCHAR(32),
    destination_country VARCHAR(32),
    idempotency_key VARCHAR(128) NOT NULL,
    retry_count INT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL,
    error_code VARCHAR(64),
    error_message VARCHAR(255),
    description TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_payment_payment_id (payment_id),
    UNIQUE KEY uk_payment_reference_number (reference_number),
    UNIQUE KEY uk_payment_idempotency_key (idempotency_key)
);

CREATE TABLE IF NOT EXISTS campaign (
    campaign_id BIGINT NOT NULL AUTO_INCREMENT,
    campaign_code VARCHAR(64) NOT NULL,
    campaign_title VARCHAR(255) NOT NULL,
    organizer_name VARCHAR(255) NOT NULL,
    category VARCHAR(64) NOT NULL,
    goal_amount DECIMAL(19, 2) NOT NULL,
    collected_amount DECIMAL(19, 2) NOT NULL DEFAULT 0,
    start_date DATE,
    end_date DATE,
    campaign_status VARCHAR(32) NOT NULL,
    description TEXT,
    created_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    PRIMARY KEY (campaign_id),
    UNIQUE KEY uk_campaign_campaign_code (campaign_code)
);

CREATE TABLE IF NOT EXISTS contribution (
    contribution_id BIGINT NOT NULL AUTO_INCREMENT,
    campaign_id BIGINT NOT NULL,
    payment_id BIGINT,
    contributor_name VARCHAR(255),
    contributor_email VARCHAR(255),
    contribution_amount DECIMAL(19, 2) NOT NULL,
    contribution_date DATETIME NOT NULL,
    contribution_status VARCHAR(32) NOT NULL,
    anonymous_donation BIT,
    message TEXT,
    receipt_number VARCHAR(64),
    PRIMARY KEY (contribution_id),
    KEY idx_contribution_campaign_id (campaign_id),
    KEY idx_contribution_payment_id (payment_id)
);

CREATE TABLE IF NOT EXISTS payment_history (
    history_id BIGINT NOT NULL AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    old_status VARCHAR(32),
    new_status VARCHAR(32) NOT NULL,
    event_type VARCHAR(64),
    remarks TEXT,
    changed_by VARCHAR(255),
    changed_at DATETIME NOT NULL,
    PRIMARY KEY (history_id),
    KEY idx_payment_history_payment_id (payment_id)
);

CREATE TABLE IF NOT EXISTS refund (
    refund_id BIGINT NOT NULL AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    refund_reference VARCHAR(64) NOT NULL,
    refund_amount DECIMAL(19, 2) NOT NULL,
    refund_method VARCHAR(32) NOT NULL,
    refund_reason VARCHAR(255) NOT NULL,
    refund_status VARCHAR(32) NOT NULL,
    initiated_by VARCHAR(32) NOT NULL,
    refund_date DATETIME NOT NULL,
    remarks TEXT,
    PRIMARY KEY (refund_id),
    UNIQUE KEY uk_refund_reference (refund_reference),
    KEY idx_refund_payment_id (payment_id)
);