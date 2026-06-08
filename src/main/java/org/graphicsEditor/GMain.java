package org.graphicsEditor;

import auth.splash.screen.SplashEvent;
import auth.onboard.OnboardingFrame;
import auth.terms.TermsFrame;
import loginSection.login.GLoginFrame;

import javax.swing.SwingUtilities;

public class GMain {


	public static void main(String[] args) {
		SwingUtilities.invokeLater(() ->
				new SplashEvent(() ->
						new OnboardingFrame(() ->
								new TermsFrame(() ->
										new GLoginFrame()
								)
						)
				)
		);
	}
}