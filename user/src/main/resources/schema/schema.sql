-- First, drop tables that have foreign keys that reference other tables
DROP TABLE IF EXISTS `shop_reservation_datetime_seats`;
DROP TABLE IF EXISTS `shop_reservations`;
DROP TABLE IF EXISTS `shop_reservation_datetimes`;
DROP TABLE IF EXISTS `reservations`;
DROP TABLE IF EXISTS `waitings`;
DROP TABLE IF EXISTS `seats`;
DROP TABLE IF EXISTS `menus`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `shop_waitings`;

-- Then, drop tables that are being referenced
DROP TABLE IF EXISTS `shops`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `regions`;


CREATE TABLE `users`
(
    `id`           bigint      NOT NULL AUTO_INCREMENT,
    `email`        varchar(63) NOT NULL,
    `role`         varchar(15) NOT NULL,
    `password`     varchar(255) NOT NULL,
    `phone_number` varchar(15) NOT NULL,
    `created_at`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`email`),
    UNIQUE KEY (`phone_number`)
);

CREATE TABLE `regions`
(
    `id`       bigint      NOT NULL AUTO_INCREMENT,
    `city`     varchar(31) NOT NULL,
    `district` varchar(31) NOT NULL
);

CREATE TABLE `shops`
(
    `id`               bigint       NOT NULL auto_increment,
    `user_id`          bigint       NOT NULL,
    `region_id`        bigint       NOT NULL,
    `name`             varchar(63)  NOT NULL,
    `description`      varchar(127) NOT NULL,
    `shop_type`        varchar(31)  NOT NULL,
    `introduce`        text         NOT NULL,
    `opening_hours`    varchar(127) NOT NULL,
    `info`             Text         NULL,
    `url`              varchar(127) NULL,
    `phone_number`     varchar(31)  NULL,
    `shop_min_price`   int          NOT NULL,
    `shop_max_price`   int          NOT NULL,
    `lunch_min_price`  int          NOT NULL,
    `lunch_max_price`  int          NOT NULL,
    `dinner_min_price` int          NOT NULL,
    `dinner_max_price` int          NOT NULL,
    `holiday`          varchar(127) NULL,
    `address`          varchar(127) NOT NULL,
    `zipcode`          varchar(7)   NOT NULL,
    `latitude`         varchar(31)  NOT NULL,
    `longitude`        varchar(31)  NOT NULL,
    `created_at`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`)
);

CREATE TABLE `categories`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT,
    `version`     bigint      NOT NULL DEFAULT 0,
    `shop_id`     bigint      NOT NULL,
    `name`        varchar(31) NOT NULL,
    `description` varchar(63) NULL,
    `min_price`   int         NOT NULL,
    `max_price`   int         NOT NULL,
    `created_at`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`shop_id`) REFERENCES `shops` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `shop_waitings`
(
    `shop_id`        bigint      NOT NULL,
    `version`        bigint      NOT NULL DEFAULT 0,
    `waiting_count`  int         NOT NULL,
    `status`         varchar(31) NOT NULL,
    `maximum`        int         NOT NULL,
    `child_enabled`  TINYINT     NOT NULL DEFAULT 0,
    `minimum_people` int         NOT NULL,
    `maximum_people` int         NOT NULL,
    `created_at`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`shop_id`),
    FOREIGN KEY (`shop_id`) REFERENCES `shops` (`id`)
);

CREATE TABLE `menus`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT,
    `category_id` bigint      NOT NULL,
    `menu_name`   varchar(31) NOT NULL,
    `price`       int         NOT NULL DEFAULT 0 COMMENT '0 이상',
    `description` varchar(63) NULL,
    `label`       varchar(15) NULL,
    `menu_type`   varchar(15) NOT NULL COMMENT '메인메뉴, 에피타이저, 코스, 정식, 오마카세 ...',
    `meal_type`   varchar(15)  NOT NULL COMMENT '점심, 저녁',
    `created_at`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON UPDATE CASCADE
);

CREATE TABLE `seats`
(
    `id`         bigint      NOT NULL AUTO_INCREMENT,
    `shop_id`    bigint      NOT NULL,
    `seat_count` int         NOT NULL,
    `seat_type`  varchar(15) NOT NULL,
    `created_at` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`shop_id`) REFERENCES `shops` (`id`)
);

CREATE TABLE `waitings`
(
    `id`              bigint      NOT NULL AUTO_INCREMENT,
    `shop_id`         bigint      NOT NULL,
    `user_id`         bigint      NOT NULL,
    `waiting_number`  int         NOT NULL,
    `issued_time`     datetime    NOT NULL,
    `waiting_status`  varchar(31) NOT NULL,
    `postponed_count` int         NOT NULL DEFAULT 0,
    `adult_count`     int         NOT NULL DEFAULT 0,
    `child_count`     int         NOT NULL DEFAULT 0,
    `created_at`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`shop_id`) REFERENCES `shop_waitings` (`shop_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE `reservations`
(
    `id`           bigint      NOT NULL AUTO_INCREMENT,
    `user_id`      bigint      NOT NULL,
    `shop_id`      bigint      NOT NULL,
    `requirement`  varchar(255),
    `person_count` int         NOT NULL,
    `status`       varchar(15) NOT NULL DEFAULT 'PROGRESS',
    `created_at`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`shop_id`) REFERENCES `shops` (`id`)
);


CREATE TABLE `shop_reservation_datetimes`
(
    `id`               bigint   NOT NULL AUTO_INCREMENT,
    `shop_id`          bigint   NOT NULL,
    `reservation_date` date     NOT NULL,
    `reservation_time` time     NOT NULL,
    `created_at`       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`shop_id`) REFERENCES `shops` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `shop_reservation_datetime_seats`
(
    `id`                           bigint      NOT NULL AUTO_INCREMENT,
    `shop_reservation_datetime_id` bigint      NOT NULL,
    `seat_id`                      bigint      NOT NULL,
    `reservation_id`               bigint,
    `seat_status`                  varchar(15) NOT NULL DEFAULT 'AVAILABLE',
    `created_at`                   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`                   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`shop_reservation_datetime_id`) REFERENCES `shop_reservation_datetimes` (`id`),
    FOREIGN KEY (`seat_id`) REFERENCES `seats` (`id`),
    FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`),
    UNIQUE KEY `seat_datetime_unique` (`shop_reservation_datetime_id`, `seat_id`)
);

CREATE TABLE `shop_reservations`
(
    `shop_id`        bigint   NOT NULL,
    `maximum_person` int      NULL,
    `minimum_person` int      NULL,
    `created_at`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`shop_id`) REFERENCES `shops` (`id`) ON UPDATE CASCADE
);