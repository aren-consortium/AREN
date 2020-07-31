<template>
    <div class="switch">
        <label>
            <i v-if='offIcon' class="material-icons">{{offIcon}}</i>
            {{offLabel}}
            <input v-bind:checked="checked"
                   @change="$emit('toggle', $event.target.checked)"
                   type="checkbox"/>
            <span class="lever"></span>
            <i v-if='onIcon' class="material-icons">{{onIcon}}</i>
            {{onLabel}}
        </label>
    </div>
</template>

<script>
    module.exports = {
        props: ['checked', 'onLabel', 'offLabel', 'offIcon', 'onIcon'],
        model: {
            prop: 'checked',
            event: 'toggle'
        }
    };
</script>