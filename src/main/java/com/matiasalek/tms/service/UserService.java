package com.matiasalek.tms.service;

import com.matiasalek.tms.dto.UserDTO;
import com.matiasalek.tms.exception.ResourceNotFoundException;
import com.matiasalek.tms.model.User;
import com.matiasalek.tms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id){
        return userRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
    }

    public User createUser(UserDTO userDTO) {
        if (userDTO.getId() != null) {
            throw new ResourceNotFoundException("User already exists");
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());

        return userRepository.save(user);
    }

    public User updateUser(Long id, UserDTO userDTO){
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userDTO.getName());
                    user.setPassword(userDTO.getPassword());
                    user.setEmail(userDTO.getEmail());
                    user.setRole(userDTO.getRole());
                    return userRepository.save(user);
                })
                .orElseThrow(()-> new ResourceNotFoundException("User not found with ID:" + id));
    }

    public void deleteUser(Long id){
        if(!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}
