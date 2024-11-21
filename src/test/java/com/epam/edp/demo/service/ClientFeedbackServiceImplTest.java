package com.epam.edp.demo.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.epam.edp.demo.dto.request.FeedbackRequestDto;
import com.epam.edp.demo.dto.response.FeedbackResponseDto;
import com.epam.edp.demo.exception.subexceptions.ValueNotValidException;
import com.epam.edp.demo.model.FeedbackEntity;
import com.epam.edp.demo.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientFeedbackServiceImplTest {

    @InjectMocks
    private ClientFeedbackServiceImpl clientFeedbackService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @Mock
    private DynamoDB dynamoDB;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Table bookingTable;
    @Mock
    UpdateItemSpec updateItemSpec;
    @Mock
    private Item item;

    private FeedbackRequestDto feedbackRequest;
    private FeedbackEntity feedbackEntity;

    @Before
    public void setUp() {
        // Initialize test data
        feedbackRequest = new FeedbackRequestDto();
        feedbackRequest.setBooking_id(123);
        feedbackRequest.setRating(5);
        feedbackRequest.setNotes("Great session!");

        feedbackEntity = new FeedbackEntity();
        feedbackEntity.setBooking_id(123);
        feedbackEntity.setFeedback_id(123);
        feedbackEntity.setCreatedAt(Instant.now().toString());
        feedbackEntity.setRating(5);
        feedbackEntity.setNotes("Great session!");
    }

    @Test
    public void testSaveClientFeedback_TokenNullOrEmpty() {
        FeedbackRequestDto feedbackRequestDto = new FeedbackRequestDto();
        // Testing for null token
        assertThrows(ValueNotValidException.class, () -> {
            clientFeedbackService.saveClientFeedback(feedbackRequestDto, null);
        }, "Token cannot be null or empty");

        // Testing for empty token
        assertThrows(ValueNotValidException.class, () -> {
            clientFeedbackService.saveClientFeedback(feedbackRequestDto, "");
        }, "Token cannot be null or empty");
    }

    @Test
    public void testSaveClientFeedback_NullFeedback() {
        // Testing for null feedback
        assertThrows(ValueNotValidException.class, () -> {
            clientFeedbackService.saveClientFeedback(null, "some-valid-token");
        }, "Feedback request cannot be null");
    }

    @Test
    public void testSaveClientFeedback_NullBookingId() {
        FeedbackRequestDto feedbackRequestDto = new FeedbackRequestDto();
        feedbackRequestDto.setRating(3);  // Set valid rating
        feedbackRequestDto.setNotes("Great service"); // Set valid notes

        // Testing for null booking_id
        assertThrows(ValueNotValidException.class, () -> {
            clientFeedbackService.saveClientFeedback(feedbackRequestDto, "valid-token");
        }, "Booking ID cannot be null");
    }

    @Test
    public void testSaveClientFeedback_NullOrInvalidRating() {
        FeedbackRequestDto feedbackRequestDto = new FeedbackRequestDto();
        feedbackRequestDto.setBooking_id(123); // Set valid booking_id
        feedbackRequestDto.setNotes("Great service"); // Set valid notes

        // Testing for null rating
        assertThrows(ValueNotValidException.class, () -> {
            clientFeedbackService.saveClientFeedback(feedbackRequestDto, "valid-token");
        }, "Rating cannot be null");

        // Testing for rating less than 1
        feedbackRequestDto.setRating(0);
        assertThrows(ValueNotValidException.class, () -> {
            clientFeedbackService.saveClientFeedback(feedbackRequestDto, "valid-token");
        }, "Rating should be between 1 and 5");

        // Testing for rating greater than 5
        feedbackRequestDto.setRating(6);
        assertThrows(ValueNotValidException.class, () -> {
            clientFeedbackService.saveClientFeedback(feedbackRequestDto, "valid-token");
        }, "Rating should be between 1 and 5");
    }

    @Test
    public void testSaveClientFeedback_NullOrEmptyNotes() {
        FeedbackRequestDto feedbackRequestDto = new FeedbackRequestDto();
        feedbackRequestDto.setBooking_id(123); // Set valid booking_id
        feedbackRequestDto.setRating(3); // Set valid rating

        // Testing for null notes
        feedbackRequestDto.setNotes(null);
        assertThrows(ValueNotValidException.class, () -> {
            clientFeedbackService.saveClientFeedback(feedbackRequestDto, "valid-token");
        }, "Notes cannot be null or empty");

        // Testing for empty notes
        feedbackRequestDto.setNotes("");
        assertThrows(ValueNotValidException.class, () -> {
            clientFeedbackService.saveClientFeedback(feedbackRequestDto, "valid-token");
        }, "Notes cannot be null or empty");
    }
    @Test
    public void testSaveClientFeedback_Success() throws JsonProcessingException {
        // Mock the behavior of external dependencies
        when(jwtUtils.extractEmail("validToken")).thenReturn("user@example.com");
        when(dynamoDB.getTable("tm2-coach-book-db")).thenReturn(bookingTable);
        when(bookingTable.getItem("id", feedbackRequest.getBooking_id())).thenReturn(item);
        when(item.getString("clientEmail")).thenReturn("user@example.com");
        when(objectMapper.convertValue(feedbackRequest, FeedbackEntity.class)).thenReturn(feedbackEntity);
        when(objectMapper.convertValue(feedbackEntity, FeedbackResponseDto.class)).thenReturn(new FeedbackResponseDto());

        // Act
        FeedbackResponseDto response = clientFeedbackService.saveClientFeedback(feedbackRequest, "validToken");
        assertNotNull(response);
        verify(dynamoDBMapper).save(feedbackEntity);
    }

    @Test(expected = ValueNotValidException.class)
    public void testSaveClientFeedback_BookingNotBelongToUser_ThrowsException() throws JsonProcessingException {
        // Mock the behavior of external dependencies
        when(jwtUtils.extractEmail("validToken")).thenReturn("user@example.com");
        when(dynamoDB.getTable("tm2-coach-book-db")).thenReturn(bookingTable);
        when(bookingTable.getItem("id", feedbackRequest.getBooking_id())).thenReturn(item);
        when(item.getString("clientEmail")).thenReturn("otherUser@example.com");

        // Act
        clientFeedbackService.saveClientFeedback(feedbackRequest, "validToken");
    }
}
