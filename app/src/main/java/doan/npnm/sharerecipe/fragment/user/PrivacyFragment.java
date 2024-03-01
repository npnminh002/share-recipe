package doan.npnm.sharerecipe.fragment.user;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentPrivacyBinding;


public class PrivacyFragment extends BaseFragment<FragmentPrivacyBinding> {


    @Override
    protected FragmentPrivacyBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentPrivacyBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        String termsAndConditions = "<h2>Terms and Conditions</h2>" +
                "<ol>" +
                "<li><strong>Acceptance of Use:</strong> Your use of our app signifies your acceptance of the following terms and conditions...</li>" +
                "<li><strong>Privacy Policy:</strong> We are committed to protecting your personal information. You agree that the personal information you provide when using our app may be collected and used according to our Privacy Policy...</li>" +
                "<li><strong>Service Usage:</strong> You agree to use our services for their intended purpose and only for personal use...</li>" +
                "<li><strong>Content:</strong> We are not responsible for user-generated content...</li>" +
                "</ol>";

        String privacyPolicy = "<h2>Privacy Policy</h2>" +
                "<ol>" +
                "<li><strong>Information Collection:</strong> We may collect personal information from you when you use our app...</li>" +
                "<li><strong>Purpose of Use:</strong> We use your personal information to provide and improve our services...</li>" +
                "<li><strong>Security:</strong> We are committed to protecting the security of your personal information...</li>" +
                "<li><strong>Access and Modification:</strong> You have the right to access and modify your personal information at any time...</li>" +
                "<li><strong>Policy Changes:</strong> We may update this Privacy Policy periodically...</li>" +
                "</ol>";

        TextView termsTextView = binding.termsTextView;
        termsTextView.setText(Html.fromHtml(termsAndConditions));

        TextView privacyTextView = binding.privacyTextView;
        privacyTextView.setText(Html.fromHtml(privacyPolicy));

    }

    @Override
    public void OnClick() {
        binding.icBack.setOnClickListener(v -> {
            closeFragment(PrivacyFragment.this);
        });
    }
}