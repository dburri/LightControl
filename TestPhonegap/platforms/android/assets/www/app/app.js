Ext.application({
    name: 'app',
    launch: function() {
        console.log('launch light control app');
        Ext.create("Ext.tab.Panel", {
            fullscreen: true,
            tabBarPosition: 'bottom',

            items: [
                {
                    title: 'Home',
                    iconCls: 'home',
                    cls: 'home',

                    html: [
                        '<img src="http://staging.sencha.com/img/sencha.png" />',
                        '<h1>Welcome to Sencha Touch</h1>',
                        "<p>You're creating the Getting Started app. This demonstrates how ",
                        "to use tabs, lists, and forms to create a simple app.</p>",
                        '<h2>Sencha Touch</h2>'
                    ].join("")
                }
            ]
        });
    }
});