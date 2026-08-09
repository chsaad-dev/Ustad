const path = require('path');

const targetUid = process.argv[2];

if (!targetUid) {
    console.error('❌ Error: Please provide your Firebase Auth UID as an argument.');
    console.error('Usage: node scripts/grant_admin_claim.js <TARGET_UID>');
    process.exit(1);
}

try {
    const serviceAccount = require(path.join(__dirname, 'serviceAccountKey.json'));
    
    const { initializeApp, cert } = require('firebase-admin/app');
    const { getAuth } = require('firebase-admin/auth');

    const app = initializeApp({ credential: cert(serviceAccount) });
    const adminAuth = getAuth(app);

    console.log(`⏳ Provisioning 'admin: true' custom claim for UID: ${targetUid}...`);

    adminAuth.setCustomUserClaims(targetUid, { admin: true })
        .then(() => {
            console.log(`\n======================================================`);
            console.log(`✅ SUCCESS! Admin custom claim (admin: true) granted!`);
            console.log(`👤 Target UID: ${targetUid}`);
            console.log(`======================================================\n`);
            process.exit(0);
        })
        .catch(async (error) => {
            if (error.code === 'auth/user-not-found') {
                console.error(`\n❌ Error: UID "${targetUid}" was not found in this Firebase project.`);
                console.log(`\n🔍 Searching for registered users in your Firebase project...\n`);
                try {
                    const listResult = await adminAuth.listUsers(10);
                    if (listResult.users.length === 0) {
                        console.log(`⚠️ No registered users exist in your Firebase Authentication project yet.`);
                        console.log(`📱 Please log into the Ustad Android App first (e.g. +92 300 1234567 / OTP 123456).`);
                    } else {
                        console.log(`Found ${listResult.users.length} registered user(s):`);
                        listResult.users.forEach((u) => {
                            console.log(`   👉 UID: ${u.uid}  |  Phone: ${u.phoneNumber || 'N/A'}  |  Email: ${u.email || 'N/A'}`);
                        });
                    }
                } catch (e) {
                    console.error('Failed to list users:', e.message);
                }
            } else {
                console.error('❌ Failed to set custom user claims:', error.message);
            }
            process.exit(1);
        });

} catch (err) {
    console.error('❌ Error:', err.message);
    process.exit(1);
}
