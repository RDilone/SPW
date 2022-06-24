
--Creando esquema del nuevo usuario
create schema sch_${user};


--creando tablas del nuevo usuario


-- DROP TABLE IF EXISTS sch_${user}.persona;

CREATE TABLE IF NOT EXISTS sch_${user}.persona
(
    id_persona serial,
    cedula text COLLATE pg_catalog."default",
    celular text COLLATE pg_catalog."default",
    direccion text COLLATE pg_catalog."default",
    foto_cedula bytea,
    nombre text COLLATE pg_catalog."default",
    num_tarjeta text COLLATE pg_catalog."default",
    pin_tarjeta integer,
    telefono text COLLATE pg_catalog."default",
    correo text COLLATE pg_catalog."default",
    CONSTRAINT persona_pk_sch_${user} PRIMARY KEY (id_persona)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS sch_${user}.persona
    OWNER to jdilonel;
    
    

-- DROP TABLE IF EXISTS sch_${user}.columna;

CREATE TABLE IF NOT EXISTS sch_${user}.columna
(
    id_columna serial,
    tabla text COLLATE pg_catalog."default",
    columna text COLLATE pg_catalog."default",
    valor integer,
    CONSTRAINT columna_pkey PRIMARY KEY (id_columna)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS sch_${user}.columna
    OWNER to jdilonel;




CREATE TABLE IF NOT EXISTS sch_${user}.prestamo
(
    id_prestamo serial,
    id_intervalo_pago integer,
    id_persona integer,
    cant_cuotas integer,
    capital_prestado numeric,
    balance_capital numeric,
    concepto text COLLATE pg_catalog."default",
    estado text COLLATE pg_catalog."default",
    fecha_final timestamp without time zone,
    fecha_inicio timestamp without time zone,
    ganancia numeric,
    porcentaje_mora numeric,
    tasa numeric,
    tipo_interes text COLLATE pg_catalog."default",
    id_intervalo_interes integer,
    plazo integer,
    desc_garantia text COLLATE pg_catalog."default",
    CONSTRAINT prestamo_pk_public PRIMARY KEY (id_prestamo),
    CONSTRAINT fk_prestamo_id_intervalo_interes_public FOREIGN KEY (id_intervalo_interes)
        REFERENCES public.intervalo (id_intervalo) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_prestamo_id_intervalo_pago_public FOREIGN KEY (id_intervalo_pago)
        REFERENCES public.intervalo (id_intervalo) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_prestamo_id_persona_public FOREIGN KEY (id_persona)
        REFERENCES sch_${user}.persona (id_persona) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS sch_${user}.prestamo
    OWNER to jdilonel;
    
    

-- DROP TABLE IF EXISTS sch_${user}.historial_prestamo;

CREATE TABLE IF NOT EXISTS sch_${user}.historial_prestamo
(
    id_historial_p serial,
    id_prestamo integer NOT NULL,
    descripcion_cambio text COLLATE pg_catalog."default",
    fecha_cambio timestamp without time zone,
    tipo_cambio text COLLATE pg_catalog."default",
    CONSTRAINT historial_prestamo_pk_sch_${user} PRIMARY KEY (id_historial_p),
    CONSTRAINT fk_historial_prestamo_sch_${user} FOREIGN KEY (id_prestamo)
        REFERENCES sch_${user}.prestamo (id_prestamo) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS sch_${user}.historial_prestamo
    OWNER to jdilonel;
    
    

-- DROP TABLE IF EXISTS sch_${user}.historial_pago;

CREATE TABLE IF NOT EXISTS sch_${user}.historial_pago
(
    id_historial_p serial,
    id_prestamo integer,
    cuota_num integer,
    cargo_mora numeric,
    interes numeric,
    capital numeric,
    fecha timestamp without time zone,
    nota_pago text COLLATE pg_catalog."default",
    CONSTRAINT historial_pago_ppk_sch_${user} PRIMARY KEY (id_historial_p),
    CONSTRAINT fk_historial_pago_id_prestamo_sch_${user} FOREIGN KEY (id_prestamo)
        REFERENCES sch_${user}.prestamo (id_prestamo) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS sch_${user}.historial_pago
    OWNER to jdilonel;
    
    

-- DROP TABLE IF EXISTS sch_${user}.archivo;

CREATE TABLE IF NOT EXISTS sch_${user}.archivo
(
    id_archivo serial,
    id_prestamo integer,
    archivo bytea,
    nombre text COLLATE pg_catalog."default",
    extension text COLLATE pg_catalog."default",
    CONSTRAINT archivos_pk_sch_${user} PRIMARY KEY (id_archivo),
    CONSTRAINT fk_archivo_id_prestamo FOREIGN KEY (id_prestamo)
        REFERENCES sch_${user}.prestamo (id_prestamo) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS sch_${user}.archivo
    OWNER to jdilonel;    
    


-- DROP TABLE IF EXISTS sch_${user}.amortizacion;

CREATE TABLE IF NOT EXISTS sch_${user}.amortizacion
(
    id_pago serial,
    id_prestamo integer,
    cuota_num integer,
    pago_capital numeric,
    pago_interes numeric,
    cuota numeric,
    saldo numeric,
    fecha timestamp without time zone,
    estado text COLLATE pg_catalog."default",
    CONSTRAINT historial_pago_pk_sch_${user} PRIMARY KEY (id_pago),
    CONSTRAINT fk_historial_pago_id_prestamo_sch_${user} FOREIGN KEY (id_prestamo)
        REFERENCES sch_${user}.prestamo (id_prestamo) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS sch_${user}.amortizacion
    OWNER to jdilonel;



--agregando registros por defecto de la tabla columna

insert into sch_${user}.columna(tabla, columna, valor) values('amortizacion','estado',1);
insert into sch_${user}.columna(tabla, columna, valor) values('amortizacion','foto_cedula',1);
insert into sch_${user}.columna(tabla, columna, valor) values('amortizacion','pago_capital',1);
insert into sch_${user}.columna(tabla, columna, valor) values('amortizacion','pago_interes',1);
insert into sch_${user}.columna(tabla, columna, valor) values('amortizacion','cuota_num',1);
insert into sch_${user}.columna(tabla, columna, valor) values('amortizacion','saldo',1);
insert into sch_${user}.columna(tabla, columna, valor) values('amortizacion','cuota',1);
insert into sch_${user}.columna(tabla, columna, valor) values('amortizacion','fecha',1);
insert into sch_${user}.columna(tabla, columna, valor) values('amortizacion','nombre',1);
insert into sch_${user}.columna(tabla, columna, valor) values('persona','telefono',0);
insert into sch_${user}.columna(tabla, columna, valor) values('persona','foto_cedula',0);
insert into sch_${user}.columna(tabla, columna, valor) values('persona','id_persona',1);
insert into sch_${user}.columna(tabla, columna, valor) values('persona','num_tarjeta',1);
insert into sch_${user}.columna(tabla, columna, valor) values('persona','pin_tarjeta',1);
insert into sch_${user}.columna(tabla, columna, valor) values('persona','correo',1);
insert into sch_${user}.columna(tabla, columna, valor) values('persona','celular',1);
insert into sch_${user}.columna(tabla, columna, valor) values('persona','cedula',1);
insert into sch_${user}.columna(tabla, columna, valor) values('persona','direccion',1);
insert into sch_${user}.columna(tabla, columna, valor) values('persona','nombre',1);


--agregando permisos de usuario por defecto

insert into perfil(id_usuario, id_permiso) values ((select id_usuario from public.usuario where usuario = UPPER(${cs}${user}${cs})),5);
insert into perfil(id_usuario, id_permiso) values ((select id_usuario from public.usuario where usuario = UPPER(${cs}${user}${cs})),14);
insert into perfil(id_usuario, id_permiso) values ((select id_usuario from public.usuario where usuario = UPPER(${cs}${user}${cs})),15);
insert into perfil(id_usuario, id_permiso) values ((select id_usuario from public.usuario where usuario = UPPER(${cs}${user}${cs})),23);
insert into perfil(id_usuario, id_permiso) values ((select id_usuario from public.usuario where usuario = UPPER(${cs}${user}${cs})),24);
insert into perfil(id_usuario, id_permiso) values ((select id_usuario from public.usuario where usuario = UPPER(${cs}${user}${cs})),13);
