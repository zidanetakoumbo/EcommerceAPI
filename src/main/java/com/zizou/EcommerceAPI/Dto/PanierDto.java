package com.zizou.EcommerceAPI.Dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PanierDto {

	private Long id;
	private String userId;
	private double totalPrice;
	private List<PanierItemDto> items = new ArrayList<>();

	// ---- Getters ----

	public Long getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public List<PanierItemDto> getItems() {
		return items;
	}

	// ---- Setters ----

	public void setId(Long id) {
		this.id = id;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setItems(List<PanierItemDto> items) {
		this.items = items;
	}
}
