/**
 * Created by Peter on 2016/10/23.
 */
// BRHC settings
var settings = {
    preferredLanguage: "cn" // en/cn: Switch language to Chinese
};

var CB_I18N;

$.ajax({
    url: "i18n/" + settings.preferredLanguage + "/brhc.json",
    type: "GET",
    dataType: "json",
    success: function(data) {
        return CB_I18N = data;
    },
    async: false
});