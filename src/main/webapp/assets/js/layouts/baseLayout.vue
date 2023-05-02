<template>
    <div v-bind="$attrs" v-on="$listeners">
        <div class="container">

            <div id="header">
                <div>
                    <!--a id="backTo" href="javascript:history.back()"><i class="material-icons">chevron_left</i></a-->
                    <h1 class="clickable" @click="$router.go(-1)"><span class="no-print">←</span></h1>
                    <slot name="title"></slot>
                </div>
                <div>
                    <slot name="right"></slot>
                </div>
            </div>

            <slot></slot>
        </div>

        <slot name="addons"></slot>

    </div>
</template>

<script>
    module.exports = {
        props: ['backTo']
    }
</script>