package io.github.tf-govstack.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.github.tf-govstack.registration.entity.ScreenDetail;
import lombok.Data;

/**
 * Composite key for {@link ScreenDetail}
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
@Data
public class ScreenDetailId implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1205506473293466764L;
	@Column(name = "id")
	private String id;
	@Column(name = "lang_code")
	private String langCode;

}
