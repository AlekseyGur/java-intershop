// package ru.alexgur.intershop.order.service;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.MockitoAnnotations;
// import org.mockito.Spy;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.web.context.WebApplicationContext;

// import ru.alexgur.intershop.TestWebConfiguration;
// import ru.alexgur.intershop.order.dto.OrderDto;
// import ru.alexgur.intershop.order.model.Order;
// import ru.alexgur.intershop.order.model.OrderItem;
// import ru.alexgur.intershop.order.service.OrderServiceImpl;
// import ru.alexgur.intershop.order.repository.OrderItemsRepository;
// import ru.alexgur.intershop.order.repository.OrderRepository;

// @ExtendWith(MockitoExtension.class)
// public class OrderServiceImplMockTest extends TestWebConfiguration {

// @Autowired
// private WebApplicationContext webApplicationContext;
// @Autowired
// private JdbcTemplate jdbc;
// @Autowired
// private OrderRepository orderRepository;
// @Autowired
// private OrderItemsRepository orderItemsRepository;
// private MockMvc mockMvc;

// @BeforeEach
// void setUp() {
// MockitoAnnotations.openMocks(this);
// }

// @Test
// void addToCart() {
// Long postId = 1L;
// String text = "Текст комментария";

// when(postRepository.checkIdExist(postId)).thenReturn(true);

// Comment savedComment = new Comment();
// savedComment.setId(1L);
// savedComment.setPostId(postId);
// savedComment.setText(text);

// when(commentRepository.add(any(Comment.class))).thenReturn(Optional.of(savedComment));

// CommentDto result = cartServiceImpl.add(postId, text);

// assertNotNull(result);
// assertEquals(savedComment.getId(), result.getId());
// assertEquals(savedComment.getPostId(), result.getPostId());
// assertEquals(savedComment.getText(), result.getText());

// verify(postRepository, times(1)).checkIdExist(postId);
// verify(commentRepository, times(1)).add(any(Comment.class));
// }

// @Test
// void getByPostId() {
// Long postId = 1L;
// String text = "Текст комментария";

// Comment comment1 = new Comment();
// comment1.setId(1L);
// comment1.setPostId(postId);
// comment1.setText(text);

// Comment comment2 = new Comment();
// comment2.setId(2L);
// comment2.setPostId(postId);
// comment2.setText(text);

// when(postRepository.checkIdExist(postId)).thenReturn(true);
// when(commentRepository.getByPostId(any(Long.class))).thenReturn(List.of(comment1,
// comment2));

// List<CommentDto> res = cartServiceImpl.getByPostId(postId);
// assertEquals(res.size(), 2);

// verify(postRepository, times(1)).checkIdExist(postId);
// verify(commentRepository, times(1)).getByPostId(postId);
// }

// @Test
// void getByWrotngPostId() {
// Long postId = 1L;

// when(postRepository.checkIdExist(postId)).thenReturn(false);

// assertThrows(Exception.class, () -> {
// cartServiceImpl.getByPostId(postId);
// });

// verify(postRepository, times(1)).checkIdExist(postId);
// }

// @Test
// void addWrotngPostId() {
// Long postId = 1L;
// String text = "Текст комментария";

// when(postRepository.checkIdExist(postId)).thenReturn(false);

// assertThrows(Exception.class, () -> {
// cartServiceImpl.add(postId, text);
// });

// verify(postRepository, times(1)).checkIdExist(postId);
// }

// @Test
// void patchWrotngCommentId() {
// Long commentId = 9999999L;
// String text = "Текст комментария";

// when(commentRepository.checkIdExist(commentId)).thenReturn(false);

// assertThrows(Exception.class, () -> {
// cartServiceImpl.patch(commentId, text);
// });

// verify(commentRepository, times(1)).checkIdExist(commentId);
// }
// }
