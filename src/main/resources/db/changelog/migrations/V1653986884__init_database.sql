--liquibase formatted sql
--changeset Kevin:1
--create_date: 16:00 31/05/2022

-- public.pz_job definition

-- Drop table

-- DROP TABLE pz_job;

CREATE TABLE pz_job
(
    id               int8         NOT NULL,
    uuid             varchar(36)  NOT NULL,
    code             varchar(50)  NOT NULL,
    name             varchar(100) NOT NULL,
    description      varchar(1000) NULL,
    class_name       varchar(100) NOT NULL,
    "type"           varchar(6)   NOT NULL,
    client_uuid      varchar(36)  NOT NULL,
    cron_expression  varchar(120) NULL,
    cron_description varchar(500) NULL,
    start_time       timestamptz NULL,
    end_time         timestamptz NULL,
    endpoint_api     jsonb NULL,
    is_deleted       bool         NOT NULL DEFAULT false,
    deleted_at       timestamptz NULL,
    deleted_by       varchar(36) NULL,
    created_at       timestamptz  NOT NULL,
    created_by       varchar(36) NULL,
    updated_at       timestamptz NULL,
    updated_by       varchar(36) NULL,
    status           varchar(20)  NOT NULL,
    CONSTRAINT job_pkey PRIMARY KEY (id),
    CONSTRAINT unique_pz_job_uuid UNIQUE (uuid)
);
CREATE UNIQUE INDEX pz_job_uuid ON public.pz_job USING btree (uuid);


-- public.pz_job_execution_history definition

-- Drop table

-- DROP TABLE pz_job_execution_history;

CREATE TABLE pz_job_execution_history
(
    id               int8        NOT NULL,
    uuid             varchar(36) NOT NULL,
    job_uuid         varchar(36) NOT NULL,
    execution_date   timestamptz NOT NULL,
    execution_status varchar(11) NOT NULL,
    error_message    text NULL,
    created_at       timestamptz NOT NULL,
    updated_at       timestamptz NULL,
    CONSTRAINT pz_job_execution_history_pkey PRIMARY KEY (id),
    CONSTRAINT unique_pz_job_execution_history_uuid UNIQUE (uuid)
);
CREATE UNIQUE INDEX pz_job_execution_history_uuid ON public.pz_job_execution_history USING btree (uuid);