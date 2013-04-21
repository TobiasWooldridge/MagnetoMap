<?php

namespace SpaceApps\MagnetBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolverInterface;

class SampleType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('latitude')
            ->add('longitude')
            ->add('reading')
            ->add('time')
        ;
    }

    public function setDefaultOptions(OptionsResolverInterface $resolver)
    {
        $resolver->setDefaults(array(
            'data_class' => 'SpaceApps\MagnetBundle\Entity\Sample'
        ));
    }

    public function getName()
    {
        return 'spaceapps_magnetbundle_sampletype';
    }
}
