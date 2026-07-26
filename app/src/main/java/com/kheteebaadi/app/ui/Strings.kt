package com.kheteebaadi.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

enum class Lang { HI, EN;
    val code get() = if (this == HI) "hi" else "en"
    companion object { fun from(code: String?) = if (code == "en") EN else HI }
}

val LocalLang = staticCompositionLocalOf { Lang.HI }

/** hi to en pairs for every UI label. Hindi-first. */
private val STR: Map<String, Pair<String, String>> = mapOf(
    "tagline" to ("सस्ता सामान · बेहतर दाम · सही सलाह" to "Cheaper inputs · Better prices · Right advice"),
    "nav_home" to ("होम" to "Home"),
    "nav_buy" to ("सामान खरीदें" to "Buy Inputs"),
    "nav_sell" to ("फसल बेचें" to "Sell Produce"),
    "nav_orders" to ("ऑर्डर" to "Orders"),
    "nav_learn" to ("सीखें" to "Learn"),
    "nav_support" to ("सहायता" to "Support"),
    "nav_profile" to ("प्रोफ़ाइल" to "Profile"),
    "nav_lead" to ("लीड ज़ोन" to "Lead Zone"),
    "nav_admin" to ("एडमिन" to "Admin"),
    "login" to ("लॉग-इन करें" to "Log in"),
    "create_account" to ("खाता बनाएं" to "Create account"),
    "mobile" to ("मोबाइल नंबर" to "Mobile number"),
    "password" to ("पासवर्ड" to "Password"),
    "create_password" to ("पासवर्ड बनाएं (कम से कम 6)" to "Create password (min 6)"),
    "name" to ("नाम" to "Name"),
    "district" to ("ज़िला" to "District"),
    "tehsil" to ("तहसील" to "Tehsil"),
    "block" to ("ब्लॉक" to "Block"),
    "village" to ("गाँव" to "Village"),
    "land_acres" to ("ज़मीन (एकड़)" to "Land (acres)"),
    "main_crops" to ("मुख्य फसलें" to "Main crops"),
    "have_account" to ("पहले से खाता है? लॉग-इन करें" to "Already have an account? Log in"),
    "no_account" to ("नया खाता बनाएं" to "Create a new account"),
    "logout" to ("लॉग आउट" to "Log out"),
    "retry" to ("दोबारा कोशिश करें" to "Retry"),
    "conn_problem" to ("कनेक्शन समस्या" to "Connection problem"),
    "loading" to ("लोड हो रहा है…" to "Loading…"),
    "greeting" to ("नमस्ते" to "Namaste"),
    "quick_actions" to ("क्या करना है?" to "What would you like to do?"),
    "add_to_cart" to ("कार्ट में डालें" to "Add to cart"),
    "cart" to ("कार्ट" to "Cart"),
    "cart_empty" to ("कार्ट खाली है" to "Your cart is empty"),
    "all" to ("सभी" to "All"),
    "market_price" to ("बाज़ार भाव" to "Market price"),
    "stock_out" to ("स्टॉक ख़त्म" to "Out of stock"),
    "brand" to ("ब्रांड" to "Brand"),
    "pack" to ("पैक" to "Pack"),
    "total" to ("कुल" to "Total"),
    "you_save" to ("आपकी बचत" to "You save"),
    "checkout" to ("भुगतान करें" to "Checkout"),
    "place_order" to ("ऑर्डर करें" to "Place order"),
    "select_address" to ("पता चुनें" to "Select delivery address"),
    "payment_method" to ("भुगतान का तरीका" to "Payment method"),
    "upi" to ("UPI" to "UPI"),
    "bank" to ("बैंक" to "Bank"),
    "cod" to ("कैश ऑन डिलीवरी" to "Cash on delivery"),
    "upi_id" to ("UPI आईडी" to "UPI ID"),
    "copy" to ("कॉपी" to "Copy"),
    "copied" to ("कॉपी हो गया ✓" to "Copied ✓"),
    "payee" to ("पाने वाला" to "Payee"),
    "scan_qr" to ("QR कोड स्कैन करें" to "Scan this QR code"),
    "i_have_paid" to ("मैंने भुगतान कर दिया — ऑर्डर करें" to "I have paid — place order"),
    "how_to_pay" to ("भुगतान कैसे करें" to "How to pay"),
    "upi_step1" to ("कोई भी UPI ऐप खोलें — PhonePe / Google Pay / Paytm" to "Open any UPI app — PhonePe / Google Pay / Paytm"),
    "upi_step2" to ("QR स्कैन करें या UPI आईडी पेस्ट करें" to "Scan the QR or paste the UPI ID"),
    "upi_step3" to ("Soubhagya को राशि भेजें" to "Send the amount to Soubhagya"),
    "upi_step4" to ("नीचे \"मैंने भुगतान कर दिया\" दबाएं" to "Tap \"I have paid\" below"),
    "open_upi_app" to ("UPI ऐप खोलें" to "Open UPI app"),
    "account_holder" to ("खाताधारक" to "Account holder"),
    "account_no" to ("खाता संख्या" to "A/C No."),
    "ifsc" to ("IFSC" to "IFSC"),
    "bank_name" to ("बैंक" to "Bank"),
    "order_placed" to ("ऑर्डर हो गया ✓" to "Order placed ✓"),
    "cancel" to ("रद्द करें" to "Cancel"),
    "remove" to ("इतिहास से हटाएं" to "Remove"),
    "my_orders" to ("मेरे ऑर्डर" to "My orders"),
    "no_orders" to ("अभी कोई ऑर्डर नहीं" to "No orders yet"),
    "sell_produce" to ("फसल बेचें" to "Sell Produce"),
    "crop" to ("फसल" to "Crop"),
    "quantity" to ("मात्रा" to "Quantity"),
    "unit" to ("इकाई" to "Unit"),
    "grade" to ("ग्रेड" to "Grade"),
    "date" to ("तारीख" to "Date"),
    "location" to ("जगह" to "Location"),
    "add_photo" to ("फ़ोटो जोड़ें (वैकल्पिक)" to "Add photo (optional)"),
    "camera" to ("कैमरा" to "Camera"),
    "gallery" to ("गैलरी" to "Gallery"),
    "submit_request" to ("अनुरोध भेजें" to "Submit request"),
    "new_listing" to ("नई फसल दर्ज करें" to "New produce request"),
    "choose_payout" to ("भुगतान कैसे लें" to "Choose how to get paid"),
    "payout_done" to ("भुगतान का तरीका चुना गया ✓" to "Payout method selected ✓"),
    "price_set_by_admin" to ("तय दाम" to "Agreed price"),
    "support" to ("सहायता" to "Support"),
    "new_support" to ("नया अनुरोध" to "New request"),
    "problem" to ("समस्या" to "Problem"),
    "description" to ("विवरण" to "Description"),
    "preferred_contact" to ("संपर्क नंबर" to "Preferred contact"),
    "specialist" to ("विशेषज्ञ" to "Specialist"),
    "my_requests" to ("मेरे अनुरोध" to "My requests"),
    "no_requests" to ("अभी कोई अनुरोध नहीं" to "No requests yet"),
    "profile" to ("प्रोफ़ाइल" to "Profile"),
    "edit_profile" to ("प्रोफ़ाइल बदलें" to "Edit profile"),
    "addresses" to ("पते" to "Addresses"),
    "add_address" to ("पता जोड़ें" to "Add address"),
    "address_line" to ("पता" to "Address line"),
    "pincode" to ("पिनकोड" to "Pincode"),
    "save" to ("सेव करें" to "Save"),
    "saved_ok" to ("जानकारी अपडेट हुई ✓" to "Details updated ✓"),
    "number_change_hint" to ("नंबर बदलने के लिए सहायता से संपर्क करें" to "Contact support to change your number"),
    "lead_zone" to ("लीड ज़ोन" to "Lead Zone"),
    "lead_zone_desc" to ("अपने गाँव के किसानों के लिए समूह ऑर्डर बनाएं और बेहतर दाम पाएं।" to "Create group orders for farmers in your village and get better prices."),
    "admin_panel" to ("एडमिन पैनल" to "Admin panel"),
    "products_admin" to ("प्रोडक्ट सूची" to "Product List"),
    "farmers" to ("किसान" to "Farmers"),
    "orders_admin" to ("ऑर्डर" to "Orders"),
    "support_admin" to ("सहायता अनुरोध" to "Support Requests"),
    "produce_admin" to ("फसल अनुरोध" to "Produce requests"),
    "total_land" to ("कुल ज़मीन" to "Total landholding"),
    "confirm_payment" to ("भुगतान पक्का करें" to "Confirm payment"),
    "make_lead" to ("लीड बनाएं" to "Make Lead"),
    "remove_lead" to ("लीड हटाएं" to "Remove Lead"),
    "reset_password" to ("पासवर्ड रीसेट" to "Reset password"),
    "edit_land" to ("ज़मीन बदलें" to "Edit land"),
    "delete" to ("मिटाएं" to "Delete"),
    "appoint_specialist" to ("विशेषज्ञ नियुक्त करें" to "Appoint specialist"),
    "set_price" to ("दाम तय करें" to "Set price"),
    "quality_check" to ("क्वालिटी जाँच" to "Quality check"),
    "schedule_pickup" to ("पिकअप तय करें" to "Schedule pickup"),
    "mark_delivered" to ("डिलीवर हुआ" to "Mark delivered"),
    "add_product" to ("प्रोडक्ट जोड़ें" to "Add product"),
    "acres" to ("एकड़" to "acres"),
    "join_date" to ("जुड़ने की तारीख" to "Joined"),
    "close" to ("बंद करें" to "Close"),
    "confirm" to ("पक्का करें" to "Confirm"),
    "required" to ("यह ज़रूरी है" to "This field is required"),
)

fun t(key: String, lang: Lang): String =
    STR[key]?.let { if (lang == Lang.HI) it.first else it.second } ?: key

@Composable
fun tr(key: String): String = t(key, LocalLang.current)

/** Canonical crops are stored in Hindi (matching the website). */
val CROPS: List<Pair<String, String>> = listOf(
    "गेहूं" to "Wheat",
    "गन्ना" to "Sugarcane",
    "आलू" to "Potato",
    "सरसों" to "Mustard",
    "मक्का" to "Maize",
    "धान" to "Paddy",
    "सब्ज़ियां" to "Vegetables",
)

fun cropLabel(canonicalHi: String, lang: Lang): String =
    if (lang == Lang.HI) canonicalHi else (CROPS.firstOrNull { it.first == canonicalHi }?.second ?: canonicalHi)

val UNITS: List<Pair<String, String>> = listOf(
    "क्विंटल" to "Quintal", "किलो" to "Kg", "टन" to "Ton", "बोरी" to "Sack"
)
val GRADES = listOf("A", "B", "C")

/** Support: 4 stages (matches website SUP_STAGES). */
val SUPPORT_STAGES: List<Pair<String, String>> = listOf(
    "प्राप्त हुआ" to "Received",
    "विश्लेषण" to "Under analysis",
    "विशेषज्ञ नियुक्त" to "Specialist assigned",
    "हल हुआ" to "Resolved",
)

/** Sell-produce: 5 stages (matches website). */
val LISTING_STAGES: List<Pair<String, String>> = listOf(
    "अनुरोध मिला" to "Request received",
    "क्वालिटी जाँच" to "Quality check",
    "पिकअप तय" to "Pickup scheduled",
    "डिलीवर हुआ" to "Delivered",
    "भुगतान" to "Paid",
)

fun stageLabel(stages: List<Pair<String, String>>, index: Int, lang: Lang): String {
    val s = stages.getOrNull(index.coerceIn(0, stages.size - 1)) ?: return ""
    return if (lang == Lang.HI) s.first else s.second
}

/** Bilingual mapping of backend error codes. */
fun errorMessage(code: String, lang: Lang): String {
    val m = mapOf(
        "bad_login" to ("मोबाइल नंबर या पासवर्ड गलत है" to "Mobile number or password is incorrect"),
        "too_many" to ("बहुत बार कोशिश की गई — 15 मिनट बाद दोबारा" to "Too many attempts, try again in 15 minutes"),
        "mobile_taken" to ("यह नंबर पहले से पंजीकृत है" to "This number is already registered"),
        "bad_mobile" to ("सही 10-अंकों का मोबाइल नंबर डालें" to "Enter a valid 10-digit mobile number"),
        "weak_password" to ("पासवर्ड कम से कम 6 अक्षर का हो" to "Password must be at least 6 characters"),
        "name_required" to ("नाम ज़रूरी है" to "Name is required"),
        "empty_response" to ("सर्वर से जवाब नहीं मिला" to "No response from server"),
        "bad_response" to ("सर्वर से ग़लत जवाब" to "Unexpected server response"),
    )
    val p = m[code] ?: ("कुछ गड़बड़ हुई — दोबारा कोशिश करें" to "Something went wrong — please try again")
    return if (lang == Lang.HI) p.first else p.second
}
