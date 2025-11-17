package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking() throws Exception {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.now().plusHours(3));
        bookingCreateDto.setEnd(LocalDateTime.now().plusHours(4));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusHours(3));
        bookingDto.setEnd(LocalDateTime.now().plusHours(4));
        bookingDto.setStatus(BookingStatus.WAITING);

        when(bookingService.add(anyLong(), any(BookingCreateDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void updateBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.update(anyLong(), anyLong(), any(Boolean.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById() throws Exception {
        Long userId = 1L;
        Long bookingId = 2L;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);

        when(bookingService.getById(eq(bookingId), eq(userId)))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    void getBookingByUser() throws Exception {
        Long userId = 1L;
        String state = "ALL";

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingService.getByUser(anyLong(), any()))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookings.getFirst().getId()));
    }

    @Test
    void getBookingByOwner() throws Exception {
        Long userId = 1L;
        String state = "ALL";

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingService.getByOwner(anyLong(), any()))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookings.getFirst().getId()));
    }
}
