start transaction read write;

-- Create jpa_knowledge table
CREATE TABLE jpa_knowledge
(
    id              bigint       not null auto_increment,
    created_at      datetime(6),
    updated_at      datetime(6),
    question_vector varchar(255) not null,
    question        varchar(255) not null,
    answer          varchar(255) not null,
    created_by      varchar(255) not null,
    PRIMARY KEY (id)
);

-- Create jpa_knowledge_resource table
CREATE TABLE jpa_knowledge_resource
(
    id            bigint       not null auto_increment,
    knowledge_id  bigint       not null,
    resource_path varchar(255) not null,
    created_at    datetime(6),
    updated_at    datetime(6),
    created_by    varchar(255) not null,
    PRIMARY KEY (id)
);

-- Create the jpa_conversation table
CREATE TABLE jpa_conversation
(
    id              bigint       not null auto_increment,
    created_at      datetime(6),
    updated_at      datetime(6),
    question_vector varchar(255) not null,
    closed          BOOLEAN,
    language        varchar(255) not null,
    rating          BOOLEAN,
    user_id         varchar(255) not null,
    knowledge_id    bigint       not null,
    incident_report_id bigint not null,
    PRIMARY KEY (id)
);

-- Create the jpa_message table
CREATE TABLE jpa_message
(
    id              bigint       not null auto_increment,
    conversation_id bigint       not null,
    message         varchar(255) not null,
    created_at      datetime(6),
    updated_at      datetime(6),
    created_by      varchar(255) not null,
    PRIMARY KEY (id)
);

-- Create the jpa_incident_report table
CREATE TABLE jpa_incident_report
(
    id              bigint not null auto_increment,
    text            varchar(255),
    created_at datetime(6),
    updated_at datetime(6),
    PRIMARY KEY (id)
);

alter table jpa_conversation
    add constraint FK29cplly21tatp0o9ccjnj32r5 foreign key (incident_report_id) references jpa_incident_report (id);
alter table jpa_message
    add constraint FK29cplly21tat20o9ccjnj32r5 foreign key (conversation_id) references jpa_conversation (id);
alter table jpa_conversation
    add constraint FK29cpl2y21tat20o9ccjnj32r5 foreign key (knowledge_id) references jpa_knowledge (id);
alter table jpa_knowledge_resource
    add constraint FK29cpllyn1tatp0o9ccjnj32r5 foreign key (knowledge_id) references jpa_knowledge (id);

commit;