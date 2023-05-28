package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.client.BaseClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(Integer userId, BookingDto bookingDto) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> getOwnerItemsBooking(Integer userId, String state, Integer from, Integer size) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("from", from);
        parameter.put("size", size);
        parameter.put("state", state);
        return get("/owner?state={state}&from={from}&size={size}", userId.longValue(), parameter);
    }

    public ResponseEntity<Object> getBooking(Integer userId, Integer bookingId) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("bookingId", bookingId);
        return get("/{bookingId}", userId.longValue(), parameter);
    }

    public ResponseEntity<Object> getBookingPages(String status, Integer userId, Integer from, Integer size) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("from", from);
        parameter.put("size", size);
        parameter.put("state", status);
        return get("?state={state}&from={from}&size={size}", userId.longValue(), parameter);
    }

    public ResponseEntity<Object> aprove(Integer userId, Integer bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId.longValue());
    }
}
