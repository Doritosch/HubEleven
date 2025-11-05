package com.hubEleven.hub.application.mapper;

import com.hubEleven.hub.application.command.CreateHubCommand;
import com.hubEleven.hub.application.command.DeleteHubCommand;
import com.hubEleven.hub.application.command.UpdateHubCommand;
import com.hubEleven.hub.application.dto.HubResult;
import com.hubEleven.hub.domain.model.Hub;
import com.hubEleven.hub.presentation.dto.request.HubCreateRequestDto;
import com.hubEleven.hub.presentation.dto.request.HubUpdateRequestDto;
import com.hubEleven.hub.presentation.dto.response.HubResponseDto;
import java.util.List;
import java.util.UUID;

public class HubMapper {

	/* domain → application */
	public static HubResult toResult(Hub hub) {
		return new HubResult(
				hub.getHubId(),
				hub.getName(),
				hub.getLocation().getAddress(),
				hub.getLocation().getLatitude(),
				hub.getLocation().getLongitude(),
				hub.getRegionCode());
	}

	/* presentation(Request) → application(Command) */
	public static CreateHubCommand toCommand(HubCreateRequestDto request) {
		return new CreateHubCommand(
				request.name(),
				request.address(),
				request.latitude(),
				request.longitude(),
				request.regionCode());
	}

	public static UpdateHubCommand toCommand(UUID hubId, HubUpdateRequestDto request) {
		return new UpdateHubCommand(
				hubId,
				request.name(),
				request.address(),
				request.latitude(),
				request.longitude(),
				request.regionCode());
	}

	public static DeleteHubCommand toCommand(UUID hubId) {
		return new DeleteHubCommand(hubId);
	}

	/* application(Result) → presentation(Response) */
	public static HubResponseDto toResponse(HubResult result) {
		return new HubResponseDto(
				result.hubId(),
				result.name(),
				result.address(),
				result.latitude(),
				result.longitude(),
				result.regionCode());
	}

	public static List<HubResponseDto> toResponseList(List<HubResult> results) {
		return results.stream().map(HubMapper::toResponse).toList();
	}
}
