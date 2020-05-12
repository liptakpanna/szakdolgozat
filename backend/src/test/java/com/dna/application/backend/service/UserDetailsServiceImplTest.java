package com.dna.application.backend.service;

import com.dna.application.backend.TestDataGenerator;
import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private TestDataGenerator testDataGenerator = new TestDataGenerator();

    @Test(expected = UsernameNotFoundException.class)
    public void loadByUsername_NotFound_Exception() {
        given(userRepository.findByUsername("username"))
                .willReturn(null);
        userDetailsService.loadUserByUsername("username");
    }

    @Test
    public void loadByUsername_Exists_ReturnUser() {
        User user = testDataGenerator.getResearcher();
        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(user);
        Assert.assertEquals(user, userDetailsService.loadUserByUsername(user.getUsername()));
    }


}
