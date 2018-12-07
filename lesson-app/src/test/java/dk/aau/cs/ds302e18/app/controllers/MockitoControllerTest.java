package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.auth.User;
import dk.aau.cs.ds302e18.app.auth.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MockitoControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUserById() {
        Optional<User> u = Optional.of(new User());
        u.get().setId(1L);
        when(userRepository.findById(1L)).thenReturn(u.get());

        Optional<User> user = userController.get(1L);

        verify(userRepository).findById(1);

        assertEquals(1L, user.get().getId());
    }
}
