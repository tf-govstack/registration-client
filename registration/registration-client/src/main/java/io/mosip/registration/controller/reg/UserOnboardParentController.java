package io.github.tf-govstack.registration.controller.reg;

import org.springframework.stereotype.Controller;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.constants.LoggerConstants;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.controller.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;


/**
 * {@code UserOnboardParentController} is to load FXML of
 * fingerprints,Iris and face.
 * 
 * @author Sravya Surampalli
 * @version 1.0
 *
 */
@Controller
public class UserOnboardParentController extends BaseController{
	
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(UserOnboardParentController.class);
	
	@FXML
	protected GridPane userOnboardId;
	
	@FXML
	private GridPane onBoardRoot;
	
	@FXML
	private ProgressIndicator progressIndicator;
	
	@FXML
	private GridPane progressIndicatorGridPane;
	
	public ProgressIndicator getProgressIndicator() {
		return progressIndicator;
	}
	
	public GridPane getParentPane() {
		return onBoardRoot;
	}
	
	public GridPane getProgressIndicatorParentPane() {
		return progressIndicatorGridPane;
	}
	
	
	public void showCurrentPage(String notTosShow, String show) {
		
		LOGGER.debug(LoggerConstants.LOG_REG_PARENT_USER_ONBOARD, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Navigating to next page based on the current page" + notTosShow + " ::: Show " + show);
		
		getCurrentPage(userOnboardId, notTosShow, show);
		
		LOGGER.debug(LoggerConstants.LOG_REG_PARENT_USER_ONBOARD, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Navigated to next page based on the current page");
		
	}

}
