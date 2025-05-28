package com.matiasalek.tms.service;

import com.matiasalek.tms.dto.UserCreateDTO;
import com.matiasalek.tms.dto.UserResponseDTO;
import com.matiasalek.tms.exception.ResourceNotFoundException;
import com.matiasalek.tms.model.User;
import com.matiasalek.tms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id){
        User userResponse = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        return new UserResponseDTO(userResponse.getId(),
                userResponse.getName(),
                userResponse.getLastName(),
                userResponse.getEmail(),
                userResponse.getRole());
    }

    public User createUser(UserCreateDTO userCreateDTO) {
        User user = new User();
        user.setName(userCreateDTO.getName());
        user.setLastName(userCreateDTO.getLastName());
        user.setPassword(userCreateDTO.getPassword());
        user.setEmail(userCreateDTO.getEmail());
        user.setRole(userCreateDTO.getRole());

        return userRepository.save(user);
    }

    public User updateUser(Long id, UserResponseDTO userResponseDTO){
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userResponseDTO.getName());
                    user.setEmail(userResponseDTO.getEmail());
                    user.setRole(userResponseDTO.getRole());
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
