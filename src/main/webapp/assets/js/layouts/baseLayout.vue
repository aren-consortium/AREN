<template>
    <div v-bind:id="id"
         @mousedown="$emit('mousedown', $event)">
        <div class="container">

            <div id="header">
                <div>
                    <!--a id="backTo" href="javascript:history.back()"><i class="material-icons">chevron_left</i></a-->
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
        props: ['id', 'backTo']
    }
</script>