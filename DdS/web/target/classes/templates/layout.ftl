<#-- layout.ftl: plantilla envoltorio. se define "content" -->
<#include "fragments/header.ftl">
<main class="container">
    ${content?if_exists}
</main>

<#include "fragments/footer.ftl">