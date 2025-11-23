package ru.practicum.shareit.booking.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class BookingController {

}
