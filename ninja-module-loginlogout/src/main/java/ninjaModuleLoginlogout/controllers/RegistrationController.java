package ninjaModuleLoginlogout.controllers;

import ninja.Context;
import ninja.Result;
import ninja.Results;

public class RegistrationController {
	
	public Result registration(Context context) {
		return Results.html();		
	}
	
	
//	// ////////////////////////////////////////////////////////////////////////
//	// Sign up (register) new user:
//	// ////////////////////////////////////////////////////////////////////////
//	/**
//	 * Show registration screen.
//	 */
//	public static void registration() {
//
//		render();
//	}
//
//	/**
//	 * Do the validation of the registration screen
//	 */
//	public static void registrationFinish(@Recaptcha String captcha,
//			@Required @Email String email,
//			@Required @Equals("confirm") @MinSize(8) String password,
//			@Required @MinSize(8) String confirm,
//			@IsTrue Boolean acceptTermsOfService) {
//
//		// check that form is really from user:
//		checkAuthenticity();
//
//		boolean hasErrors = true;
//
//		if (Play.id.equals("test")) {
//
//			// we are in test mode.. ignore wrong captcha
//			if (validation.errors().size() == 1) {
//
//				if (validation.errors().get(0).getKey().equals("captcha")) {
//					hasErrors = false;
//				}
//			}
//
//		} else {
//
//			hasErrors = validation.hasErrors();
//
//		}
//
//		// check if user exists and don't allow to register user:
//		if (!hasErrors) {
//
//			if (Casino.doesUserExist(email)) {
//				hasErrors = true;
//
//			}
//		}
//
//		// save or display error
//		if (hasErrors) {
//
//			flash.error("registration.error");
//			validation.keep();
//			params.flash("email");
//
//			// If acceptTermsOfService is not checked it is not sent to server..
//			// therefore we check for null...
//			if (acceptTermsOfService != null) {
//				params.flash("acceptTermsOfService");
//			}
//
//			registration();
//
//		} else {
//
//			String passwordHash = Casino.getHashForPassword(password);
//			String confirmationCode = Casino.shortUUID();
//
//			Casino.createNewCasinoUser(email, passwordHash,
//					confirmationCode);
//
//			RegistrationMailer.confirmation(email, confirmationCode);
//			pending();
//
//		}
//
//	}
//
	/**
	 * registration was successful => show message to user that an email was
	 * sent for final activation of user account.
	 */
	public Result registrationPending(Context context) {

		return Results.html();
	}

	/**
	 * Checks and activates user account with code sent in email.
	 */
	public Result registrationConfirm(Context context) {

		String code = context.getParameter("code");
		
		if (code.length() == 0) {
			context.getFlashCookie().error("registration.error");

		}

		// get user with this email

		String email = Casino
				.getCasinoUserWithConfirmationCode(code);

		if (email != null) {

			Casino.deleteConfirmationCodeOfCasioUser(email);

			// we also log in user after successful confirmation of email...
			context.getSessionCookie().put("username", email);

			context.getFlashCookie().success("registration.registration_success");
		} else {

			context.getFlashCookie().error("registration.error");
		}

		return Results.html();
	}

	// ////////////////////////////////////////////////////////////////////////
	// Recover lost password of user using email:
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Show input window for email.
	 */
	public Result lostPassword(Context context) {

		return Results.html();

	}
//
//	/**
//	 * Validate the input of email... check recaptcha and so on...
//	 */
//	public static void lostPasswordFinish(@Required @Email String email,
//			@Recaptcha String captcha) {
//
//		// /check:
//		checkAuthenticity();
//
//		boolean hasErrors = true;
//
//		if (Play.id.equals("test")) {
//
//			// we are in test mode.. ignore wrong captcha
//			if (validation.errors().size() == 1) {
//
//				if (validation.errors().get(0).getKey().equals("captcha")) {
//					hasErrors = false;
//				}
//			}
//
//		} else {
//
//			hasErrors = validation.hasErrors();
//
//		}
//
//		// save or display error
//		if (hasErrors) {
//
//			flash.error("registration.error");
//			params.flash("email");
//			validation.keep();
//			lostPassword();
//
//		} else {
//
//			// check if user exists and send email
//			// always display success message...
//
//			if (Casino.doesUserExist(email)) {
//				// user found.. displaying notification.
//				// send notification
//
//				String recoverPasswordCode = Casino.shortUUID();
//				
//				Casino.setRecoveryPasswordCode(email, recoverPasswordCode);
//
//				RegistrationMailer.lostPassword(email, recoverPasswordCode);
//
//				// display notification to user
//				lostPasswordEmailSentCheckInbox();
//
//			} else {
//				// System.out.println("user not found.. displaying notification anyway.");
//				// display success anyway don't allow robots to check which
//				// emails are signed up.
//				lostPasswordEmailSentCheckInbox();
//
//			}
//
//		}
//
//	}
//
//	/**
//	 * email lookup was successful => tell user to check inbox to re-enable
//	 * password.
//	 */
//	public static void lostPasswordEmailSentCheckInbox() {
//
//		render();
//
//	}
//
//	/**
//	 * User has valid confirmation => we allow to generate new password
//	 */
//	public static void lostPasswordNewPassword(@Required String code) {
//
//		render(code);
//
//	}
//
//	/**
//	 * Validation for the lostPasswordNewPassword screen
//	 */
//	public static void lostPasswordNewPasswordFinish(@Required String code,
//			@Recaptcha String captcha,
//			@Required @Equals("passwordConfirm") @MinSize(8) String password,
//			@Required @MinSize(8) String passwordConfirm) {
//
//		// check:
//		checkAuthenticity();
//
//		boolean hasErrors = true;
//
//		if (Play.id.equals("test")) {
//
//			// we are in test mode.. ignore wrong captcha
//			if (validation.errors().size() == 1) {
//
//				if (validation.errors().get(0).getKey().equals("captcha")) {
//					hasErrors = false;
//				}
//			}
//
//		} else {
//
//			hasErrors = validation.hasErrors();
//
//		}
//
//		// save or display error
//		if (hasErrors) {
//
//			flash.error("registration.error");
//			params.flash("password");
//			params.flash("passwordConfirm");
//			validation.keep();
//			lostPasswordNewPassword(code);
//
//		} else {
//
//			String email = Casino
//					.getCasinoUserWithRecoveryPasswordCode(code);
//
//			if (email == null) {
//				// hmm. does not exist. display success anyway...
//				// we don't want to let robots sniff...
//
//				render();
//				
//
//			} else {
//
//				Casino.setRecoveryPasswordCode(email, code);
//
//				// compute hash...
//				String passwordHash = Casino.getHashForPassword(password);
//
//				Casino.setNewPasswordHashForUser(email,
//						passwordHash);
//
//				render();
//
//			}
//
//		}
//
//	}

}
