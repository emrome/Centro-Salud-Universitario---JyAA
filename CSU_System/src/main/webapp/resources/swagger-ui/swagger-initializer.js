window.onload = function () {
  const ctx = window.location.pathname.split('/resources/')[0];
  window.ui = SwaggerUIBundle({
    url: ctx + "/api/openapi",
    dom_id: '#swagger-ui',
    deepLinking: true,
    presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset],
    plugins: [SwaggerUIBundle.plugins.DownloadUrl],
    layout: "StandaloneLayout"
  });
};