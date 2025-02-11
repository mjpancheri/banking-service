create table if not exists endereco(
    id serial primary key,
    rua text not null,
    logradouro text not null,
    complemento text not null,
    numero int not null
);

create table if not exists agencia(
    id serial primary key,
    nome text not null,
    razao_social text not null,
    cnpj text not null,
    endereco_id int references endereco
);

alter table if exists Agencia alter column id set data type bigint;
alter table if exists Agencia alter column cnpj set data type varchar(255);
alter table if exists Agencia alter column nome set data type varchar(255);
alter table if exists Agencia alter column razao_social set data type varchar(255);
alter table if exists Agencia alter column endereco_id set data type bigint;
alter table if exists Endereco alter column id set data type bigint;
alter table if exists Endereco alter column complemento set data type varchar(255);
alter table if exists Endereco alter column logradouro set data type varchar(255);
alter table if exists Endereco alter column rua set data type varchar(255);