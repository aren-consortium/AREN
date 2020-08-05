<template>
    <label class="input-field inline search-box">
        <i class="material-icons prefix">search</i>
        <input v-bind:value="value" v-on:input="$emit('input', $event.target.value)" type="text"/>
        <label v-if="label" for="search">Recherche</label>
    </label>
</template>

<script>
    module.exports = {
        props: ['value', 'label']
    };
</script>