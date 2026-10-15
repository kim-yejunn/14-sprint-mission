package com.sprint.mission.discodeit.user.dto;

public record UserUpdateRequestDto(
    String newUsername,
    String newPassword,
    String newEmail) {

}
