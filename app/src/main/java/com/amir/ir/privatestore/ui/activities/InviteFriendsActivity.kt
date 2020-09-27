package com.amir.ir.privatestore.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.databinding.ActivityInviteFriendsBinding
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.setClipboard
import com.amir.ir.privatestore.utils.toast

class InviteFriendsActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityInviteFriendsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_friends)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_invite_friends)
        initViews()
    }

    private fun initViews() {
        val inviteCode = UserConfigs.user.value!!.inviteCode //todo age user login nakarde bud dar navigationDrawer gozine davat az doostan nabayad neshan dadae beshe
        mBinding.inviteCode = inviteCode
        mBinding.ivCopy.setOnClickListener {
            setClipboard(this, inviteCode)
            toast("کد دعوت در حافظه کپی شد!")
        }
        mBinding.ivShare.setOnClickListener {
            try {

                val domain = resources.getString(R.string.domain)

                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name_fa)
                var shareBody = resources.getString(R.string.app_name_fa)
                    .toString() + " ... " + resources.getString(R.string.text_shoar)
                shareBody = "$shareBody\n\nکد معرف : "
                shareBody += inviteCode
                shareBody = "$shareBody\nلینک ثبت نام : "
                shareBody = """${shareBody}http://$domain/register?code=$inviteCode""".trimIndent()
                shareBody += "لینک دانلود اپلیکیشن : "
                shareBody += "https://$domain/downloadmobileapp"
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(sharingIntent, "انتخاب کنید"))
            } catch (e: Exception) {
                //e.toString();
            }
        }

        mBinding.frameIvBack.setOnClickListener { onBackPressed() }
    }

}
